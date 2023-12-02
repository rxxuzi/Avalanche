package ui

import javafx.application.{Application, Platform}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.image.Image
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.scene.web.WebView
import javafx.stage.Stage
import net.OpenHTML
import net.html.HtmlParser
import util.Gen

import java.net.{MalformedURLException, URL}
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class Gui extends Application {
  override def start(primaryStage: Stage): Unit = {
    val webView = new WebView()
    val webEngine = webView.getEngine

    val backButton = new Button("Back")
    backButton.setOnAction(_ => {
      if (webEngine.getHistory.getCurrentIndex > 0) {
        webEngine.getHistory.go(-1)
      }
    })

    val reloadButton = new Button("Reload")
    reloadButton.setOnAction(_ => webEngine.reload())

    val forwardButton = new Button("Forward")
    forwardButton.setOnAction(_ => {
      if (webEngine.getHistory.getCurrentIndex < webEngine.getHistory.getEntries.size() - 1) {
        webEngine.getHistory.go(1)
      }
    })

    val urlField = new TextField("https://www.google.com")
    urlField.setOnAction(_ => webEngine.load(urlField.getText))
    urlField.setPrefWidth(500) // URLバーの長さを600に設定

    val searchButton = new Button("Go")

    // 正しいURLが入力されていない場合、Googleで文字列を検索
    searchButton.setOnAction(_ => {
      try {
        new URL(urlField.getText)
        webEngine.load(urlField.getText)
      } catch {
        case _: MalformedURLException =>
          webEngine.load("https://www.google.com/search?q=" + urlField.getText.replace(" ", "+"))
      }
    })

    urlField.setOnAction(_ => searchButton.fire())

    val statusLabel = new Label("")

    // 保存するボタン
    val saveButton = new Button("Save")
    // "Save" ボタンのアクションハンドラ内
    saveButton.setOnAction(_ => {
      val url: String = webEngine.getLocation
      val html = OpenHTML(url, async = true)

      // HTMLコンテンツを非同期で取得
      val htmlContentFuture = html.getPageContent

      // HTMLコンテンツの取得が完了したら、ファイルに保存
      htmlContentFuture.onComplete {
        case Success(content) =>
          Platform.runLater(() => {
            // UIスレッドでの処理
            val doc = HtmlParser(content)
            val gen = Gen(doc.getText)
            gen.saveToTxt(doc.getTitle)
            statusLabel.setText("Success")
          })
        case Failure(exception) =>
          Platform.runLater(() => {
            // エラー処理: UIスレッドでの処理
            println(exception.getMessage)
            statusLabel.setText("Failure")
          })
      }(ExecutionContext.global)
    })


    val buttonBox = new HBox(saveButton, statusLabel)

    // CSSを使用して広告や動画を非表示にする
    try{
      webEngine.setUserStyleSheetLocation(getClass.getResource("/css/hide-elements.css").toString)
    }catch {
      case _: Throwable => println("CSS file not found.")
    }

    // ナビゲーションバーの設定
    val navigationBar = new HBox(5, backButton, reloadButton, forwardButton, urlField, searchButton, buttonBox)
    navigationBar.setStyle("-fx-padding: 10;")

    val layout = new VBox(5, navigationBar, webView)

    // ウェブビューが垂直方向に自動的にサイズを調整するように設定
    VBox.setVgrow(webView, Priority.ALWAYS)

    val scene = new Scene(layout, 1600, 900)
    primaryStage.setScene(scene)
    primaryStage.setTitle("Avalanche")
    primaryStage.show()

    // アプリケーションのアイコンを設定
    val iconStream = getClass.getResourceAsStream(global.Config.APP_ICON)
    if (iconStream != null) {
      primaryStage.getIcons.add(new Image(iconStream))
    } else {
      println("Icon file not found.")
    }

    // 初期URLを読み込む
    webEngine.load(urlField.getText)

    webEngine.locationProperty.addListener(new ChangeListener[String]() {
      override def changed(observable: ObservableValue[_ <: String], oldValue: String, newValue: String): Unit = {
        urlField.setText(newValue)
      }
    })
  }
}

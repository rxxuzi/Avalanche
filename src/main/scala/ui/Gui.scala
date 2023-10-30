package ui

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.{Button, TextField}
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.scene.web.WebView
import javafx.stage.Stage
import javafx.scene.image.Image

import java.net.{MalformedURLException, URL}

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

    val jsToggleButton = new Button("Disable JS")
    jsToggleButton.setOnAction(_ => {
      val currentSetting = webEngine.isJavaScriptEnabled
      webEngine.setJavaScriptEnabled(!currentSetting)
      jsToggleButton.setText(if (!currentSetting) "Disable JS" else "Enable JS")
      println(s"JavaScript is now ${if (!currentSetting) "enabled" else "disabled"}")
    })

    // CSSを使用して広告や動画を非表示にする
    try{
      webEngine.setUserStyleSheetLocation(getClass.getResource("/css/hide-elements.css").toString)
    }catch {
      case _: Throwable => println("CSS file not found.")
    }

    // ナビゲーションバーの設定
    val navigationBar = new HBox(5, backButton, reloadButton, forwardButton, urlField, searchButton, jsToggleButton)
    navigationBar.setStyle("-fx-padding: 10;")

    val layout = new VBox(5, navigationBar, webView)

    // ウェブビューが垂直方向に自動的にサイズを調整するように設定
    VBox.setVgrow(webView, Priority.ALWAYS)

    val scene = new Scene(layout, 1600, 900)
    primaryStage.setScene(scene)
    primaryStage.setTitle("Avalanche")
    primaryStage.show()

    // アプリケーションのアイコンを設定
    val iconStream = getClass.getResourceAsStream("/image/icon/Avalanche.png")
    if (iconStream != null) {
      primaryStage.getIcons.add(new Image(iconStream))
    } else {
      println("Icon file not found.")
    }

    // 初期URLを読み込む
    webEngine.load(urlField.getText)
  }
}

package ui

import io.{Html, Json, Text}
import javafx.application.Application
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Scene
import javafx.scene.control.{Button, ComboBox, TextField}
import javafx.scene.image.Image
import javafx.scene.layout.{HBox, Priority, VBox}
import javafx.scene.web.WebView
import javafx.stage.Stage
import net.OpenHTML
import net.download.MediaDownloader

import java.net.{MalformedURLException, URI, URL}
import java.time.ZonedDateTime

class Gui extends Application {
  val json = new Json

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

    val saveOptions = new ComboBox[String]()
    saveOptions.getItems.addAll("Text", "Image", "HTML")
    saveOptions.setValue("Text")

    // 保存するボタン
    val saveButton = new Button("Save")
    // "Save" ボタンのアクションハンドラ内
    saveButton.setOnAction(_ => {
      val url: String = webEngine.getLocation
      val openhtml =  new OpenHTML(url, async = true)
      val hx = openhtml.getHtml
      val selectedOption = saveOptions.getValue

      try{
        val doc = openhtml.toHTMLParser

        selectedOption match {
          case "Text"
          =>
            val txt = Text(doc.getText)
            txt.save(doc.getTitle)
            json.addData(url, txt.getPath)
            println("Txtで保存" + txt.getPath)
          case "Image"
          =>
            val medias = doc.getImages
            val downloader = new MediaDownloader()
            downloader.downloads(medias)

          case "HTML"
          => val html = Html(hx)
            html.save(doc.getTitle)
            json.addData(url, html.getPath)
          println("Htmlで保存" + html.getPath)
        }
      }catch {
        case e: Exception => println(e.getMessage)
//        case _: Throwable =>
//          println("Failure" )
      }
    })


    val buttonBox = new HBox(saveButton, saveOptions)

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

    //アプリを閉じたときの動作
    primaryStage.setOnCloseRequest(_ =>{
      import java.time.format.DateTimeFormatter

      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      val formatted = formatter.format(ZonedDateTime.now())

      json.save(formatted)
      println("Save Json : " + formatted)
    })

  }
}

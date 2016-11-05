import java.io.BufferedReader; 
import java.io.IOException; 
import java.io.InputStreamReader; 
import javax.jms.JMSException; 
import javax.jms.Message; 
import javax.jms.MessageListener; 
import javax.jms.Session; 
import javax.jms.TextMessage; 
import javax.jms.Topic; 
import javax.jms.TopicConnection; 
import javax.jms.TopicConnectionFactory; 
import javax.jms.TopicPublisher; 
import javax.jms.TopicSession; 
import javax.jms.TopicSubscriber; 
import javax.naming.InitialContext; 
import javax.naming.NamingException;

/**  チャット・クライアント　メインクラス * JMSクライアントとして、チャットメッセージの送受信機能、ユーザインタフェース * 機能を提供する。
 */

public class ChatClient implements MessageListener { 
/** コネクション・オブジェクト */
 TopicConnection connection = null; 
/** パブリッシャ・オブジェクト */ 
private TopicPublisher publisher = null; 
/** サブスクライバ・オブジェクト */ 
private TopicSubscriber subscriber = null; 
/** 送信用セッション・オブジェクト */ 
private TopicSession pubSession = null; 
/** 受信用セッション・オブジェクト */ 
private TopicSession subSession = null; 


 /** * コンストラクタ。 
* JMSサーバとの間にコネクション、セッションを確立し、メッセージの送受信を 
* 開始する。 
*/

 public ChatClient() { 
try { 
// 初期コンテキストの取得
 InitialContext jndi = new InitialContext(); 
// コネクション生成オブジェクトの取得 
TopicConnectionFactory conFactory = (TopicConnectionFactory)jndi.lookup("TopicConnectionFactory"); 
// JMSサーバとのコネクションを生成 
connection = conFactory.createTopicConnection(); 
// セッションの生成 
// スレッディング制約のため、送信用と受信用の２セッション生成する。 
// 送信処理用セッションの生成 
pubSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
 // 受信処理用セッションの生成 
subSession = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE); 
// トピックの取得 
// "Favorite"はJMSサーバへの登録名と同じにする。 
Topic chatTopic = (Topic)jndi.lookup("Favorite");
 // Subscriberオブジェクトの生成 subscriber = subSession.createSubscriber(chatTopic); 
// Publisherオブジェクトの生成 
publisher = pubSession.createPublisher(chatTopic);
 // メッセージ受信用Listenerの登録 subscriber.setMessageListener(this); 
// コネクションの開始 connection.start();
 } catch(NamingException ex) { 
// JNDI検索失敗 
ex.printStackTrace(); 
} catch(JMSException ex) { 
// メッセージング初期化失敗 
ex.printStackTrace(); 
} 
}
/** * メイン・メソッド。 
* ChatClientオブジェクトの生成、チャットメッセージの入力の受付と 
* 送信処理の呼び出しを行う。 
* ユーザから"exit"が入力されたら、プログラムを終了する。 
* @param args 起動引数 
*/

 public static void main(String[] args) { 
// チャットクライアントクラスの生成 
ChatClient client = new ChatClient(); 
// コマンドラインから文字列を読み込むReaderを生成 
BufferedReader commandLine = new BufferedReader(new InputStreamReader(System.in)); 
// 終了が入力されるまで、コマンドラインからメッセージを読んで
 // サーバに送信する処理を繰り返す。 
System.out.println("チャットを開始します..."); 
while(true) { 
String msg = null; 
try { 
// コマンドラインから１行読み込み 
msg = commandLine.readLine(); 
} catch(IOException ex) { 
// コマンド読み込みエラーは処理を中断 
ex.printStackTrace(); break;
 } 
// "exit"が入力されたら、ループを抜ける 
if(msg.equalsIgnoreCase("exit")) {
 break; 
} 
// サーバにメッセージを送る。 
client.writeMessage(msg); 
} 
// 接続を閉じる。 
client.close(); System.exit(0);
 }

 /** * 受信メッセージ処理 
* 受信したチャットメッセージを標準出力に出力する。 
* @param message 受信メッセージ 
*/

public void onMessage(Message message) { 
// メッセージを表示 
TextMessage textMessage = (TextMessage)message; 
try { 
System.out.println("RCV > " + textMessage.getText());
 } catch(JMSException ex) { 
ex.printStackTrace(); 
}
 }
/** * チャットメッセージ送信メソッド 
* チャットメッセージを生成し、JMSサーバに送信する。 
* @param msg 送信メッセージ 
*/

 public void writeMessage(String msg) { 
try { 
// チャット文字列の送信 
TextMessage message = pubSession.createTextMessage(); 
message.setText(msg);
 publisher.publish(message);
 } catch(JMSException ex) {
 ex.printStackTrace();
 } 
}

/** * 接続クローズメソッド。 
* JMSサーバとのコネクションを切断する。 
*/

 public void close() { 
try { 
// コネクション切断 
connection.close(); 
} catch(JMSException ex) { 
ex.printStackTrace(); 
} 
}

}

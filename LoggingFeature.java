import javax.ws.rs.GET;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import knowledgebank.rest.service.KnowledgeResource;


//Rest
@Provider
public class LoggingFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        //フィルターを適用するメソッドを制御する
        //configureメソッドは各リソースメソッドがディプロイされるたびに一度だけ呼ばれる
        //コールバックメソッドです
        //featureContextのregisterで適応するフィルターや、インターセプターを登録する
        if (resourceInfo.getResourceClass().equals(KnowledgeResource.class)
                //ResourceInfoは、リクエストでマッチングしたリソースクラスや、リソースメソッドへアクセスする
                //ためのインターフェイス。getResourceClassメソッドでリソースクラスを取得。
                //getResourceMethodでリソースメソッドを取得する。                
                && resourceInfo.getResourceMethod().isAnnotationPresent(GET.class)) {
            featureContext.register(ServerSideLoggingFilter.class);
        }
    }
}
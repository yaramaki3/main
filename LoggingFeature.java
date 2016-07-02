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
        //�t�B���^�[��K�p���郁�\�b�h�𐧌䂷��
        //configure���\�b�h�͊e���\�[�X���\�b�h���f�B�v���C����邽�тɈ�x�����Ă΂��
        //�R�[���o�b�N���\�b�h�ł�
        //featureContext��register�œK������t�B���^�[��A�C���^�[�Z�v�^�[��o�^����
        if (resourceInfo.getResourceClass().equals(KnowledgeResource.class)
                //ResourceInfo�́A���N�G�X�g�Ń}�b�`���O�������\�[�X�N���X��A���\�[�X���\�b�h�փA�N�Z�X����
                //���߂̃C���^�[�t�F�C�X�BgetResourceClass���\�b�h�Ń��\�[�X�N���X���擾�B
                //getResourceMethod�Ń��\�[�X���\�b�h���擾����B                
                && resourceInfo.getResourceMethod().isAnnotationPresent(GET.class)) {
            featureContext.register(ServerSideLoggingFilter.class);
        }
    }
}
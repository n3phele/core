package n3phele.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;

public interface UploadPanel extends IsWidget {

	public void setSignature(String url, String destination, String keyId,
			String base64Policy, String sign, String content);

	public boolean isCancelled();

}
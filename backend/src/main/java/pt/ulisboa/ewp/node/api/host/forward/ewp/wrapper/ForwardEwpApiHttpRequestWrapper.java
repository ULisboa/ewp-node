package pt.ulisboa.ewp.node.api.host.forward.ewp.wrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * A wrapper of HttpServletRequest interface that allows multiple body readings using the method
 * getBody(). The body obtained is as was sent by client.
 */
public class ForwardEwpApiHttpRequestWrapper extends ContentCachingRequestWrapper {

  private String body;
  private ServletInputStream inputStream;

  public ForwardEwpApiHttpRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);

    // NOTE: this is necessary to force cache creation for parameters (namely for multipart requests)
    // otherwise those are lost during processing
    request.getParameterMap();

    initBody(request);
    initInputStream();
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (this.inputStream == null) {
      return super.getInputStream();
    }
    return this.inputStream;
  }

  private void initBody(HttpServletRequest request) throws IOException {
    if (HttpMethod.POST.matches(request.getMethod())) {
      StringHttpMessageConverter converter = new StringHttpMessageConverter(
          StandardCharsets.UTF_8);
      this.body = converter.read(String.class, new ServletServerHttpRequest(this));
    } else {
      this.body = "";
    }
  }

  private void initInputStream() {
    this.inputStream = new ContentCachingInputStream(body.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Returns request body. It may be called multiple times.
   */
  public String getBody() {
    return body;
  }

  /**
   * Reference: https://stackoverflow.com/questions/30484388/inputstream-to-servletinputstream/33836552
   */
  private class ContentCachingInputStream extends ServletInputStream {

    private final byte[] bytes;

    private int lastIndexRetrieved = -1;
    private ReadListener readListener = null;

    private ContentCachingInputStream(byte[] bytes) {
      this.bytes = bytes;
    }

    @Override
    public int read() throws IOException {
      int i;
      if (!isFinished()) {
        i = bytes[lastIndexRetrieved + 1];
        lastIndexRetrieved++;
        if (isFinished() && (readListener != null)) {
          try {
            readListener.onAllDataRead();
          } catch (IOException ex) {
            readListener.onError(ex);
            throw ex;
          }
        }
        return i;
      } else {
        return -1;
      }
    }

    @Override
    public boolean isFinished() {
      return (lastIndexRetrieved == bytes.length - 1);
    }

    @Override
    public boolean isReady() {
      // This implementation will never block
      // We also never need to call the readListener from this method, as this method will never return false
      return isFinished();
    }

    @Override
    public void setReadListener(ReadListener listener) {
      this.readListener = listener;
      if (!isFinished()) {
        try {
          readListener.onDataAvailable();
        } catch (IOException e) {
          readListener.onError(e);
        }
      } else {
        try {
          readListener.onAllDataRead();
        } catch (IOException e) {
          readListener.onError(e);
        }
      }
    }
  }
}

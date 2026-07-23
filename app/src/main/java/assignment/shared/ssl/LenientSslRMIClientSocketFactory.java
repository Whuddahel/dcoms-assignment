package assignment.shared.ssl;

import java.io.IOException;
import java.net.Socket;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.rmi.ssl.SslRMIClientSocketFactory;

public class LenientSslRMIClientSocketFactory extends SslRMIClientSocketFactory {
  private static final long serialVersionUID = 1L;

  public LenientSslRMIClientSocketFactory() {
    super();
  }

  @Override
  public Socket createSocket(String host, int port) throws IOException {
    Socket socket = super.createSocket(host, port);
    if (socket instanceof SSLSocket) {
      SSLSocket sslSocket = (SSLSocket) socket;
      SSLParameters sslParams = sslSocket.getSSLParameters();
      // Disabling endpoint identification which skips hostname verification
      sslParams.setEndpointIdentificationAlgorithm("");
      sslSocket.setSSLParameters(sslParams);
    }
    return socket;
  }
}

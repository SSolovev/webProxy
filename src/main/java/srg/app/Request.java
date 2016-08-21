package srg.app;

/**
 * Created by Sergey on 01.08.2016.
 */
public class Request {

    private RequestElement method;
    private String methodValue;
    private String accept;
    private String acceptEncoding;
    private String range;
    private String userAgent;
    private String proxyConnection;
    private String host;
    private int port = 80;
    private String unnamed = "";

    private String sourceString;

    public int getPort() {
        return port;
    }

    public String getSourceString() {
        return sourceString;
    }

    public RequestElement getMethod() {
        return method;
    }

    public String getMethodValue() {
        return methodValue;
    }

    public String getAccept() {
        return accept;
    }

    public String getAcceptEncoding() {
        return acceptEncoding;
    }

    public String getRange() {
        return range;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getProxyConnection() {
        return proxyConnection;
    }

    public String getHost() {
        return host;
    }

    public String getUnnamed() {
        return unnamed;
    }

    public static Request parseRequestFromString(String val) {
        if (val != null && !val.isEmpty()) {
            return new Request(val);
        } else {
            return null;
        }
    }

    public Request(String requestString) {
        this.sourceString = requestString;
        String[] arrayString = requestString.split("\n");
        for (int i = 0; i < arrayString.length; i++) {
            if (i == 0) {
                method = RequestElement.fromString(arrayString[i]);
                methodValue = arrayString[i].split(method.getValue())[1].trim();
            } else {
                String[] row = arrayString[i].split(":");
                if (row.length >= 2) {
                    switch (row[0]) {
                        case "Accept":
                            this.accept = row[1].trim();
                            break;
                        case "Accept-Encoding":
                            this.acceptEncoding = row[1].trim();
                            break;
                        case "Range":
                            this.range = row[1].trim();
                            break;
                        case "User-Agent":
                            this.userAgent = row[1].trim();
                            break;
                        case "Proxy-Connection":
                            this.proxyConnection = row[1].trim();
                            break;
                        case "Host":
                            this.host = row[1].trim();
                            if (row.length > 2) {
                                this.port = Integer.parseInt(row[2].trim());
                            }
                            break;
                        default:
                            this.unnamed += ";" + row[0] + ":" + row[1];
                    }
                }

            }
        }
    }

    @Override
    public String toString() {
        return sourceString;
//        return "Request{" +
//                "method=" + method +
//                ", methodValue='" + methodValue + '\'' +
//                ", accept='" + accept + '\'' +
//                ", acceptEncoding='" + acceptEncoding + '\'' +
//                ", range='" + range + '\'' +
//                ", userAgent='" + userAgent + '\'' +
//                ", proxyConnection='" + proxyConnection + '\'' +
//                ", host='" + host + '\'' +
//                ", unnamed='" + unnamed + '\'' +
//                '}';
    }
}




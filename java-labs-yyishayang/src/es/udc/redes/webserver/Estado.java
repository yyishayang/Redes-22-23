package es.udc.redes.webserver;

public enum Estado {
    OK("200 OK\n"),
    NOT_MODIFIED("304 Not Modified\n"),
    BAD_REQUEST("400 Bad Request\n"),
    NOT_FOUND("404 Not Found\n");

    private final String state;
    Estado(String state){
        this.state = state;
    }

    public String getEstado(){
        return state;
    }
}

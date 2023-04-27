package es.udc.redes.webserver;

import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ServerThread extends Thread {
    private final Socket socket;
    private Estado estado = null;
    private boolean ifMod = false;

    public ServerThread(Socket s) {
        // Store the socket s
        this.socket = s;
    }

    public void run() {
        try {
            // This code processes HTTP requests and generates HTTP responses
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            String line = input.readLine();
            String [] tokens;
            String ifModSince = null;

            if(line != null) {
                tokens = line.split(" ");

                while (!line.equals("")) {
                    if (line.contains("If-Modified-Since")) {
                        ifMod = true;
                        ifModSince = line.substring(19);
                        break;
                    }
                    line = input.readLine();
                }
                output.println(processResquest(tokens, ifModSince, ifMod));

                String method = tokens[0];
                if ("GET".equals(method) || estado == Estado.BAD_REQUEST) {
                    get(estado, tokens[1]);
                }
            }

            input.close();
            output.close();

        } catch (SocketTimeoutException e) {
            System.err.println("Nothing received in 300 secs");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Close the client socket
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Comprueba en qué sistema operativo está
    public String system(){
        if(((String)System.getProperties().get("os.name")).contains("Windows")){
            return "\\";
        }else
            return "/";
    }

    //Comprobaciones iniciales
    public boolean correct(String[] tokens){
        return (tokens.length == 3) &&
                (tokens[0].equals("GET") || tokens[0].equals("HEAD"));
    }

    //Comprobar si la fecha de modificación fue actualizada
    boolean isModified(String ifModSince, File file) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

        if (!ifModSince.isEmpty()) {
            Date ifModSinceDate = sdf.parse(ifModSince);
            String lastMod = sdf.format(file.lastModified());
            Date lastModDate = sdf.parse(lastMod);

            return (lastModDate.after(ifModSinceDate));
        }
        return false;
    }

    //Línea de estado de la respuesta
    public String codeLine(Estado estado){
        return "HTTP/1.0 " + estado.getEstado();
    }

    //Líneas de cabecera de la respuesta
    public String headerLines(File file) throws IOException {
        Path path = Paths.get(file.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

        String fecha = "Date: " + sdf.format(new Date()) + "\n";
        String servidor = "Server: Webserver\n";
        String modif = "Last-Modified: " + sdf.format(file.lastModified()) + "\n";
        String tam = "Content-Length: " + file.length() + "\n";
        String tipo = "Content-Type: " + Files.probeContentType(path) + "\n";

        return fecha + servidor + modif + tam + tipo;
    }

    //Mostrar el cuerpo del fichero strfile
    private void readFile(String strfile) throws IOException {
        File file = new File(strfile);
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(file));
        BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
        int c;

        while ((c = input.read()) != -1) {
            output.write(c);
        }

        input.close();
        output.close();
    }

    public void get(Estado estado, String file) throws IOException {
        switch (estado) {
            case BAD_REQUEST -> readFile("p1-files"+system()+"error400.html");
            case NOT_FOUND -> readFile("p1-files" +system()+ "error404.html");
            default -> readFile("p1-files"+ system() + file);
        }
    }

    //Procesar la respuesta de la petición
    public String processResquest(String[] tokens, String ifModSince, boolean ifMod) throws ParseException, IOException {
        File fileError400 = new File("p1-files" + system()+ "error400.html");
        File fileError404 = new File("p1-files"+ system()+ "error404.html");

        if(!correct(tokens)) {
            estado = Estado.BAD_REQUEST;
            return codeLine(estado) + headerLines(fileError400);
        }

        File file = new File("p1-files" +system()+ tokens[1]);
        if (file.exists()) {
            if(ifMod && !isModified(ifModSince, file))
                estado = Estado.NOT_MODIFIED;
            else
                estado = Estado.OK;
        } else {
            estado = Estado.NOT_FOUND;
            return codeLine(estado) + headerLines(fileError404);
        }

        return codeLine(estado) + headerLines(file);
    }
}

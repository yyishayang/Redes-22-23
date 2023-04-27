package es.udc.redes.tutorial.info;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Info {
    public void info(String fichero) throws IOException {
        Path path = Paths.get(fichero);
        BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);

        System.out.println("Nombre: "+path.getFileName());
        System.out.println("Tamanio: "+attr.size());
        System.out.println("Fecha de última modificación: "+attr.lastModifiedTime());
        System.out.println("Tipo: "+Files.probeContentType(path));

        if(fichero.contains(".")){
            System.out.println("Extensión: "+ fichero.substring(fichero.lastIndexOf('.')+1));
        }
        System.out.println("Ruta absoluta: "+path.toAbsolutePath());


    }

    public static void main(String[] args) throws IOException {
        Info fichero = new Info();
        fichero.info(args[0]);

    }
    
}

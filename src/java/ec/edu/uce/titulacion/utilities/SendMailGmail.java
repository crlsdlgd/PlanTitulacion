package ec.edu.uce.titulacion.utilities;

import ec.edu.uce.titulacion.dao.UsuarioDaoImpl;
import ec.edu.uce.titulacion.entidades.Plan;
import ec.edu.uce.titulacion.entidades.Usuario;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.ejb.EJB;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMailGmail {

    private String correoEnvia = "gestiontitulacionuce@gmail.comm"; //correo emisor
    private String claveCorreo = "Pass_1992";//contraseña de correo emisor
    private Properties properties = new Properties();

    private String factorAutenticacion = "";
    private Usuario user;

    @EJB
    UsuarioDaoImpl dao;

    public SendMailGmail() {
        user = new Usuario();
        //user.setEmail("estudiantetitulacion@gmail.com");
        //user.setNombre("Estudiante");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.user", correoEnvia);
        properties.put("mail.password", claveCorreo);
    }

    public void send(String host, String port, final String userName, final String password, String toAddress,
            String subject, String htmlBody, Map<String, String> mapInlineImages)
            throws AddressException, MessagingException {
        // sets SMTP server properties

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = {new InternetAddress(toAddress)};
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(htmlBody, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);

        // adds inline image attachments
        if (mapInlineImages != null && mapInlineImages.size() > 0) {
            Set<String> setImageID = mapInlineImages.keySet();

            for (String contentId : setImageID) {
                MimeBodyPart imagePart = new MimeBodyPart();
                imagePart.setHeader("Content-ID", "<" + contentId + ">");
                imagePart.setDisposition(MimeBodyPart.INLINE);

                String imageFilePath = mapInlineImages.get(contentId);
                try {
                    imagePart.attachFile(imageFilePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                multipart.addBodyPart(imagePart);
            }
        }

        msg.setContent(multipart);

        Transport.send(msg);
    }

    public void enviarMail() {
        // SMTP info
        String host = properties.getProperty("mail.smtp.host");
        String port = properties.getProperty("mail.smtp.port");

        // message info
        //String mailTo = user.getEmail();
        String mailTo = "estudiantetitulacion@gmail.com";
        String subject = "RECORDATORIO DE TEMA DE TESIS";
        StringBuffer body = new StringBuffer("<html><br>");
        body.append("<img src=\"cid:image1\" style=\"width:100%;height:100px\"/>");
        body.append("<form style=\"padding: 0px 14px 0px 14px;border-bottom:none !important;border-top:none !important;border: solid 1px red;\"> Estimado Estudiate.<br><br>");
        //body.append(user.getNombre() + "<br>");
        body.append("Le recordamos que usted consta en el sistema de titulación<br><br>");
        body.append("A la Fecha: " + new Date() + "<br>");
        body.append("Con el tutor: " + new Date() + "<br>");
        body.append("Con el tema de: " + factorAutenticacion + " <br><br><br>");
        body.append("Gracias por utilizar nuestros servicios.<br><br><br>");
        body.append("Atentamente,<br>");
        body.append("Facultad de Ingeniería Ciencias Físicas y Matemática, Dirección de Carrera de Ingeniería Informática.</form>");
        body.append("<img src=\"cid:image2\" style=\"width:100%;height:130px\" /><br>");
        body.append("</html>");

        // inline images
        Map<String, String> inlineImages = new HashMap<String, String>();
        inlineImages.put("image1", "D:/Eilfil/Imágenes/asd.png");//Imagen de cabecera
        inlineImages.put("image2", "D:/Eilfil/Imágenes/asd.png");//Imagen de pie

        try {
            send(host, port, correoEnvia, claveCorreo, mailTo, subject, body.toString(), inlineImages);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }

    public void enviarPrimerMail(Plan plan) throws Exception {
        try {
            List<Usuario> lista = dao.listarUserByPlan(plan);
            for (int i = 0; i < lista.size(); i++) {
                enviarPrimerMailUsuario(lista.get(i), plan);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void enviarSegundoMail(Plan plan) throws Exception {
        UsuarioDaoImpl dao = new UsuarioDaoImpl();
        try {
            List<Usuario> lista = dao.listarUserByPlan(plan);
            for (int i = 0; i < lista.size(); i++) {
                enviarSegundoMailUsuario(lista.get(i), plan);
            }
        } catch (Exception e) {
            throw e;
        }

    }

    public void enviarTercerMail(Plan plan) throws Exception {

        //UsuarioDaoImpl dao = new UsuarioDaoImpl();
        try {
            List<Usuario> lista = dao.listarUserByPlan(plan);
            for (int i = 0; i < lista.size(); i++) {
                enviarTercerMailUsuario(lista.get(i), plan);
            }
        } catch (Exception e) {
            throw e;
        }

    }
    
    public void enviarPrimerMailUsuario(Usuario user, Plan plan) {
        // SMTP info
        String host = properties.getProperty("mail.smtp.host");
        String port = properties.getProperty("mail.smtp.port");

        // message info
        //String mailTo = user.getEmail();
        String mailTo = user.getEmail();
        String subject = "REGISTRO DE TEMA DE TESIS";
        StringBuffer body = new StringBuffer("<html><br>");
        body.append("<img src=\"cid:image1\" style=\"width:100%;height:100px\"/>");
        body.append("<form style=\"padding: 0px 14px 0px 14px;border-bottom:none !important;border-top:none !important;border: solid 1px red;\"> Estimado Estudiate.<br><br>");
        //body.append(user.getNombre() + "<br>");
        body.append("El día de hoy se ha registrado en el sistema de titulación el tema: " + plan.getTema() + " <br><br>");
        body.append("A la Fecha: " + plan.getFecha() + "<br>");
        body.append("Con el tutor: " + new Date() + "<br>");
        body.append("Recuerde que tiene seis meses para culminar este proyecto<br>");
        body.append("Gracias por utilizar nuestros servicios.<br><br><br>");
        body.append("Atentamente,<br>");
        body.append("Facultad de Ingeniería Ciencias Físicas y Matemática, Dirección de Carrera de Ingeniería Informática.</form>");
        body.append("<img src=\"cid:image2\" style=\"width:100%;height:130px\" /><br>");
        body.append("</html>");

        // inline images
        Map<String, String> inlineImages = new HashMap<String, String>();
        inlineImages.put("image1", "D:/Eilfil/Imágenes/asd.png");//Imagen de cabecera
        inlineImages.put("image2", "D:/Eilfil/Imágenes/asd.png");//Imagen de pie

        try {
            send(host, port, correoEnvia, claveCorreo, mailTo, subject, body.toString(), inlineImages);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }

    public void enviarSegundoMailUsuario(Usuario user, Plan plan) {
        // SMTP info
        
        System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        String host = properties.getProperty("mail.smtp.host");
        String port = properties.getProperty("mail.smtp.port");

        // message info
        String mailTo = user.getEmail();
        String subject = "RECORDATORIO DE TEMA DE TESIS";
        StringBuffer body = new StringBuffer("<html><br>");
        body.append("<img src=\"cid:image1\" style=\"width:100%;height:100px\"/>");
        body.append("<form style=\"padding: 0px 14px 0px 14px;border-bottom:none !important;border-top:none !important;border: solid 1px red;\"> Estimado Estudiate.<br><br>");
        //body.append(user.getNombre() + "<br>");
        body.append("Le recordamos que usted consta en el sistema de titulación<br><br>");
        body.append("A la Fecha: " + plan.getFecha() + "<br>");
        body.append("Con el tutor: " + new Date() + "<br>");
        body.append("Con el tema de: " + plan.getTema() + " <br><br><br>");
        body.append("Gracias por utilizar nuestros servicios.<br><br><br>");
        body.append("Atentamente,<br>");
        body.append("Facultad de Ingeniería Ciencias Físicas y Matemática, Dirección de Carrera de Ingeniería Informática.</form>");
        body.append("<img src=\"cid:image2\" style=\"width:100%;height:130px\" /><br>");
        body.append("</html>");

        // inline images
        Map<String, String> inlineImages = new HashMap<String, String>();
        inlineImages.put("image1", "D:/Eilfil/Imágenes/asd.png");//Imagen de cabecera
        inlineImages.put("image2", "D:/Eilfil/Imágenes/asd.png");//Imagen de pie

        try {
            send(host, port, correoEnvia, claveCorreo, mailTo, subject, body.toString(), inlineImages);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }

    public void enviarTercerMailUsuario(Usuario user, Plan plan) {
        // SMTP info
        String host = properties.getProperty("mail.smtp.host");
        String port = properties.getProperty("mail.smtp.port");

        // message info
        //String mailTo = user.getEmail();
        String mailTo = user.getEmail();
        String subject = "RECORDATORIO DE TEMA DE TESIS";
        StringBuffer body = new StringBuffer("<html><br>");
        body.append("<img src=\"cid:image1\" style=\"width:100%;height:100px\"/>");
        body.append("<form style=\"padding: 0px 14px 0px 14px;border-bottom:none !important;border-top:none !important;border: solid 1px red;\"> Estimado Estudiate.<br><br>");
        //body.append(user.getNombre() + "<br>");
        body.append("Le recordamos que usted consta en el sistema de titulación<br><br>");
        body.append("A la Fecha: " + plan.getFecha() + "<br>");
        body.append("Con el tutor: " + new Date() + "<br>");
        body.append("Con el tema de: " + plan.getTema() + " <br>");
        body.append("Recuerde ya han culminado cinco meses desde el registro de su tema, le queda un mes para el control antiplagio. <br>");
        body.append("Gracias por utilizar nuestros servicios.<br><br><br>");
        body.append("Atentamente,<br>");
        body.append("Facultad de Ingeniería Ciencias Físicas y Matemática, Dirección de Carrera de Ingeniería Informática.</form>");
        body.append("<img src=\"cid:image2\" style=\"width:100%;height:130px\" /><br>");
        body.append("</html>");

        // inline images
        Map<String, String> inlineImages = new HashMap<String, String>();
        inlineImages.put("image1", "D:/Eilfil/Imágenes/asd.png");//Imagen de cabecera
        inlineImages.put("image2", "D:/Eilfil/Imágenes/asd.png");//Imagen de pie

        try {
            send(host, port, correoEnvia, claveCorreo, mailTo, subject, body.toString(), inlineImages);
            System.out.println("Email sent.");
        } catch (Exception ex) {
            System.out.println("Could not send email.");
            ex.printStackTrace();
        }
    }

    /**
     * SETTERS Y GETTERS *
     */
    public Usuario getUser() {
        return user;
    }

    public void setUser(Usuario user) {
        this.user = user;
    }

    public String getFactorAutenticacion() {
        return factorAutenticacion;
    }

    public void setFactorAutenticacion(String factorAutenticacion) {
        this.factorAutenticacion = factorAutenticacion;
    }

    public void setFactorAutentication(String pass) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

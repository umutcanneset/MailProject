package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.xml.soap.Text;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    @FXML
    private TabPane tabPane;
    @FXML
    private Tab inbox;
    @FXML
    private Tab sendMail;
    @FXML
   private TextField toField;

    @FXML
    private  TextField subjectField;

    @FXML
    private TextArea messageField;

    @FXML
    private Button sendButton;
    @FXML
    private Button attachmentButton;

    private  String fileAdress;

    @FXML
    private TextField dosyaAdres;

    @FXML
    private ListView<String> receiveArea;

    private ObservableList<String> messageList;

    private String fileName;

    @FXML
    private TextField messageFrom;

    @FXML
    private TextField messageSubject;

    @FXML
    private TextArea messageText;

    @FXML
    private Button saveAttachment;

    private Folder inboxx;

    private MimeBodyPart mmpart;

    @FXML
    private Button reply;



    public void login(){

    }

        @FXML
    private  void sendMail(ActionEvent actionEvent){

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.from", "cse482atisik@gmail.com");
            props.put("mail.smtp.auth", "true");
            Authenticator a = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("cse482atisik@gmail.com", "EmineEkin");
                }
            };

            Session session = Session.getDefaultInstance(props, a);
            try {
                MimeMessage msg = new MimeMessage(session);
                msg.setFrom();
                msg.setRecipients(Message.RecipientType.TO, toField.getText());
                msg.setSubject(subjectField.getText());
                msg.setSentDate(new Date());




                if(fileName!=null) {


                    Multipart mp = new MimeMultipart();
                    BodyPart bp = new MimeBodyPart();
                    bp.setText(messageField.getText());
                    mp.addBodyPart(bp);
                    BodyPart partForAtt = new MimeBodyPart();

                    DataSource source = new FileDataSource(fileName);
                    partForAtt.setDataHandler(new DataHandler(source));
                    partForAtt.setFileName(fileName);
                    partForAtt.setFileName(fileName);
                    mp.addBodyPart(partForAtt);
                    msg.setContent(mp);
                }else{
                    msg.setText(messageField.getText());
                }






                Transport.send(msg);
                toField.setText("");
                messageField.setText("");
                subjectField.setText(" ");
                dosyaAdres.setText("");
                System.out.println("gonderdim mesaji");
            } catch (MessagingException mex) {
                System.out.println("send failed, exception: " + mex);
            }
        }
        @FXML
        private void addAttachment(){
           FileChooser fc=new FileChooser();
           File selectedFile=fc.showOpenDialog(null);
            fileAdress = selectedFile.getAbsolutePath();
            fileName= fileAdress.replace("\\","\\\\");




            dosyaAdres.setEditable(false);

            dosyaAdres.setText(fileName);



        }

        public void receiveMail(){
            //Define Protocol
            receiveArea.getItems().clear();
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            //Create Session
            Session session = Session.getInstance(props, null);
            try {
                Store store = session.getStore("imaps");
                store.connect("imap.gmail.com", "cse482atisik@gmail.com", "EmineEkin");


                Folder[] f = store.getDefaultFolder().list();


                inboxx = store.getFolder("INBOX");
                inboxx.open(Folder.READ_ONLY);





                int mesCount = inboxx.getMessageCount();





                while (mesCount != 0) {
                    Message mess = inboxx.getMessage(mesCount);

                 // reading content - I've kept it as it is for future usage
                if (mess.isMimeType("text/plain")) {

                    receiveArea.getItems().add(mess.getFrom()[0] + " " + mess.getSubject());


                }/*else if (mess.isMimeType("text/html")) {
                        String html = (String) mess.getContent();
                       receiveArea.getItems().add() "\n" + org.jsoup.Jsoup.parse(html).text();
                       */


                else if (mess.isMimeType("multipart/*")) {

                    MimeMultipart mimeMultipart = (MimeMultipart) mess.getContent();
                    String result = "";
                    int count = mimeMultipart.getCount();
                    for (int i = 0; i < count; i++) {
                        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                        if (bodyPart.isMimeType("text/plain")) {
                            result = result + "\n" + bodyPart.getContent();
                        ;

                        } else if (bodyPart.getContent() instanceof MimeMultipart) {
                            result = result + (MimeMultipart) bodyPart.getContent();
                        }
                    }
                    //receiveArea.getItems().add("Subject: " + mess.getSubject());
                    //fromTf.setText("From: " + msg.getFrom()[0].toString());
                    receiveArea.getItems().add(mess.getFrom()[0] + " " + mess.getSubject());



                } else {
                    receiveArea.getItems().add("Your e-mail client can not read this type of message");
                }
                mesCount--;
            }
                String[] messageBox= new String[mesCount];

                for (int i=1; i<mesCount; i++){
                    messageBox[i]=(inboxx.getMessage(i).getFrom()[0].toString()+ "/ "+inboxx.getMessage(i).getSubject()+ "/"+ inboxx.getMessage(i).getContent()); ;

                }
                messageList.addAll(Arrays.asList(messageBox));

                System.out.println("System refreshed");

            } catch (Exception mex) {
                mex.printStackTrace();
            }
        }



        public void showMails(){
            reply.setVisible(true);
            int mesIndex= receiveArea.getSelectionModel().getSelectedIndex();


            try {
                int mesCount=inboxx.getMessageCount();
                Message mess = inboxx.getMessage(mesCount-mesIndex);
                messageFrom.setText(mess.getFrom()[0].toString());
                messageSubject.setText(mess.getSubject());
                if (mess.getContent() instanceof Multipart) {

                    Multipart mp = (Multipart) mess.getContent();
                    int partCount = mp.getCount();
                    for (int j = 0; j < partCount; j++) {

                        mmpart = (MimeBodyPart) mp.getBodyPart(j); //get the body part
                        if (Part.ATTACHMENT.equalsIgnoreCase(mmpart.getDisposition())) {
                            //so we have a file in this part

                            saveAttachment.setVisible(true);


                        } else {
                            //body part has some content
                            messageText.setText(mmpart.getContent().toString());

                        }


                    }
                }else{
                    messageText.setText(mess.getContent().toString());
                    saveAttachment.setVisible(false);
                }
            }catch (Exception exx){
                exx.printStackTrace();
            }

            }

            @FXML
            public void downloadAttachment(){
            try{
                mmpart.saveFile(mmpart.getFileName());
                saveAttachment(mmpart);
                System.out.println("Attachment saved");

            }catch(Exception ex){
                ex.printStackTrace();
                }
            }

            public void saveAttachment(MimeBodyPart mmpart){
             try{
                 InputStream inputStream=mmpart.getInputStream();
                 File saveFile=new File("C:\\Users\\User\\Downloads",mmpart.getFileName());
                 FileOutputStream fileOutputStream= new FileOutputStream(saveFile);
                 byte[] buf= new byte[4096];
                 int bytesRead;
                 while((bytesRead= inputStream.read(buf))!=-1){
                     fileOutputStream.write(buf,0,bytesRead);
                 }
                 fileOutputStream.close();

             }catch(Exception ex){
                 ex.printStackTrace();
             }
            }
            @FXML
            public void replyMessage(){
                tabPane.getSelectionModel().select(sendMail);
                toField.setText(messageFrom.getText());
                subjectField.setText("RE:"+ messageSubject.getText());
                messageField.setText("wrote:"+"\n"+ messageText.getText());


            }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        messageList= FXCollections.observableArrayList();
        receiveArea.setItems(messageList);
        saveAttachment.setVisible(false);
        dosyaAdres.setEditable(false);
        messageFrom.setEditable(false);
        messageSubject.setEditable(false);
        messageText.setEditable(false);
        reply.setVisible(false);

        receiveMail();



    }

}


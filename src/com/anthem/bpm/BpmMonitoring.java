package com.anthem.bpm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BpmMonitoring {
  public static void main(String[] args) throws ClassNotFoundException, SQLException {
    Class.forName("oracle.jdbc.driver.OracleDriver");
    Connection con = DriverManager.getConnection(
        "jdbc:oracle:thin:@//fnetpengn-p-01.internal.das:1525/fnetpep", "F_SW", "wAb!Y8Ngjq3Tklo9mPUR%d1s_");
    Statement stmt = con.createStatement();
    String sql1 = "select count(*) from PEVQ1_ACES_ERROR_RECOVERY";
    String sql2 = "select count(*) from PEVQ1_ACES_PEND_HOLD";
    String sql3 = "select count(*) from PEVQ1_CCCEN_ERROR_RECOVERY";
    String sql4 = "select count(*) from PEVQ1_CCCEN_PEND_HOLD  where QRECPTDT > ((CAST(SYS_EXTRACT_UTC(SYSTIMESTAMP) AS DATE) - TO_DATE('01/01/1970','DD/MM/YYYY')) * 24 * 60 * 60)-10800";
    String sql5 = "select count(*) from PEVQ1_CCCEN_PEND_HOLD  where QRECPTDT < ((CAST(SYS_EXTRACT_UTC(SYSTIMESTAMP) AS DATE) - TO_DATE('01/01/1970','DD/MM/YYYY')) * 24 * 60 * 60)-10800";
    String sql6 = "select count(*) from PEVQ1_CE_OPERATIONS";
    String sql7 = "select count(*) from PEVQ1_CONTENTOPERATIONS";
    String queue1 = "ACES_ERROR_RECOVERY";
    String queue2 = "ACES_PEND_HOLD";
    String queue3 = "CCCEN_ERROR_RECOVERY";
    String queue4 = "CCCEN_PEND_HOLD( greater than current date)";
    String queue5 = "CCCEN_PEND_HOLD( less than current date) ";
    String queue6 = "CE_OPERATIONS";
    String queue7 = "CONTENT_OPERATIONS";
    ArrayList<String> sqlList = new ArrayList<>();
    ArrayList<String> queueList = new ArrayList<>();
    ArrayList<String> resultList = new ArrayList<>();
    sqlList.add(sql1);
    sqlList.add(sql2);
    sqlList.add(sql3);
    sqlList.add(sql4);
    sqlList.add(sql5);
    sqlList.add(sql6);
    sqlList.add(sql7);
    queueList.add(queue1);
    queueList.add(queue2);
    queueList.add(queue3);
    queueList.add(queue4);
    queueList.add(queue5);
    queueList.add(queue6);
    queueList.add(queue7);
    for (String s : sqlList) {
      ResultSet rs = stmt.executeQuery(s);
      rs.next();
      resultList.add(rs.getString(1));
      System.out.println(rs.getString(1));
    } 
    String recipient = "DL-FileNetLightsOnSupport@anthem.com";
    String recipient2 = "Steven.Wallman@anthem.com";
    String sender = "DL-FileNetLightsOnSupport@anthem.com";
    String host = "smtp.wellpoint.com";
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    Session session = Session.getDefaultInstance(properties);
    try {
      MimeMessage message = new MimeMessage(session);
      message.setFrom((Address)new InternetAddress(sender));
      message.addRecipient(Message.RecipientType.TO, (Address)new InternetAddress(recipient));
      message.addRecipient(Message.RecipientType.CC, (Address)new InternetAddress(recipient2));
      message.setSubject("BPM2 Monitoring");
      StringBuilder builder = new StringBuilder();
      builder.append("<html><body>Hi Team,<br>We could see the below Work Items count. Please look into this.<br><br><table border=1><tr><th>Queue Name</th><th>Count</th></tr>");
      for (int i = 0; i < queueList.size(); i++) {
        builder.append("<tr>");
        builder.append("<td>");
        builder.append(queueList.get(i));
        builder.append("</td>");
        builder.append("<td>");
        builder.append(resultList.get(i));
        builder.append("</td>");
        builder.append("</tr>");
      } 
      con.close();
      builder.append("</table><br>Regards,<br>FileNet LightsOn Support Team </body></html>");
      String result = builder.toString();
      System.out.println(result);
      message.setContent(result, "text/html");
      Transport.send((Message)message);
      System.out.println("Mail successfully sent");
    } catch (MessagingException mex) {
      mex.printStackTrace();
    } 
  }
}

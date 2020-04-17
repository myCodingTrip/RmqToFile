package com.bonc;

import com.bonc.util.FileUtil;
import com.rabbitmq.client.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class RmqToFile {
    public static void main(String[] args) throws Exception {

        int i = 0;
        String host = args[i++];
        int port = Integer.parseInt(args[i++]);
        String virtualHost = args[i++];
        String username = args[i++];
        String password = args[i++];
        boolean durable = Boolean.parseBoolean(args[i++]);
        String queueName = args[i++];
        //rmq的编码
        final String queueEncoding = args[i++];
        //输出文件的路径
        String dataPath = args[i++];
        int min = Integer.parseInt(args[i++]);

        final File outputFile = new File(dataPath);
        if (outputFile.exists()){
            outputFile.delete();
        }
        outputFile.createNewFile();
        //final File countFile = new File(countPath);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, durable, false, false, null);
        System.out.println("Start successfully.To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {

            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                String message = new String(body, queueEncoding);
                FileUtils.writeStringToFile(outputFile, message + "\n", "utf-8", true);
            }
        };
        channel.basicConsume(queueName, true, consumer);

        FileUtil.monitorFile(outputFile, min);
    }
}

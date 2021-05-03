package com.example.demo.controller;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.example.demo.dao.BookCrudDao;
import com.example.demo.dao.BookCrudDaoImpl;
import com.example.demo.entity.Book;
import com.example.demo.mapper.BooksMapperImpl;
import com.example.demo.service.BookCrudService;
import com.example.demo.service.BookCrudServiceImpl;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.messaging.MessagingAutoConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

import static org.mockito.Mockito.mock;

@TestConfiguration
@EnableAutoConfiguration(exclude = {MessagingAutoConfiguration.class, ContextStackAutoConfiguration.class})
public class TestConfig {
    @Bean
    public BookCrudController bookCrudController() {
        return new BookCrudController();
    }

    @Bean
    public AmazonDynamoDB amazonDynamoDB() {
        int port = 8000;
        DynamoDBProxyServer server;       //create a server locally.
        try {
            server = ServerRunner.createServerFromCommandLineArgs(new String[]{"-inMemory", "-port", String.valueOf(port)});
            server.start();     //start the server on available port.
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:" + port, "us-east-1"))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("access", "secret")))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public DynamoDBMapper dynamoDBMapper(AmazonDynamoDB db) {
        DynamoDBMapperConfig builder = new DynamoDBMapperConfig.Builder()
                .build();
        return new DynamoDBMapper(db, builder);
    }

    @PostConstruct
    public void createTables() {
        System.setProperty("sqlite4java.library.path", "native-libs"); // to use the sql4lite copied libraries in the folder defined in maven plugin.
        CreateTableRequest myTableRequest = dynamoDBMapper(amazonDynamoDB()).generateCreateTableRequest(Book.class);
        myTableRequest.setProvisionedThroughput(new ProvisionedThroughput(1000L, 1500L)); // 2
        amazonDynamoDB().createTable(myTableRequest);
    }

    @Bean
    public Book book() {
        return new Book();
    }

    @Bean
    public BooksMapperImpl booksMapper() {
        return new BooksMapperImpl();
    }

    @Bean
    public BookCrudService bookCrudService() {
        return new BookCrudServiceImpl(queueMessagingTemplate(), bookCrudDao(), booksMapper());
    }

    @Bean
    public BookCrudDao bookCrudDao() {
        return new BookCrudDaoImpl(dynamoDBMapper(amazonDynamoDB()));
    }

    @Bean(name = "amazonSQSAsync")
    public AmazonSQSAsync amazonSQSAsync() {
        return mock(AmazonSQSAsync.class);
    }

    @Bean
    public QueueMessagingTemplate queueMessagingTemplate() {
        return new QueueMessagingTemplate(amazonSQSAsync());
    }
}

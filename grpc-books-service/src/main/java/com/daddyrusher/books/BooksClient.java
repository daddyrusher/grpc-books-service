package com.daddyrusher.books;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.logging.Logger;

public class BooksClient {
    private static final Logger LOGGER = Logger.getLogger(BooksClient.class.getName());

    private final BookServiceGrpc.BookServiceBlockingStub blockingStub;

    public BooksClient(Channel channel) {
        blockingStub = BookServiceGrpc.newBlockingStub(channel);
    }

    public void list() {
        Books.Empty empty = Books.Empty.newBuilder().build();
        Books.BookList response;

        try {
            response = blockingStub.list(empty);
        } catch (StatusRuntimeException e) {
            LOGGER.info("RPC failed: can't get book list!");
            return;
        }
        LOGGER.info("BookList: " + response.getBooksList());
    }

    public void insert(Books.Book book) {
        Books.Empty response;

        try {
            response = blockingStub.insert(book);

        } catch (StatusRuntimeException e) {
            LOGGER.info("RPC failed: can't insert new data!");
            return;
        }
        LOGGER.info("saved new book: " + book);
        LOGGER.info("Response: " + response);
    }

    public static void main(String[] args) {
        String target = "localhost:50051";

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                .usePlaintext()
                .build();

        try {
            BooksClient client = new BooksClient(channel);
            Books.Book book = Books.Book.newBuilder().setId(1334).setTitle("new book").setAuthor("kek ivanov").build();
            Books.Book book1 = Books.Book.newBuilder()
                    .setId(123)
                    .setTitle("A Fairy tale")
                    .setAuthor("Maksim Zanin")
                    .build();
            client.insert(book);
            client.insert(book1);
            client.list();
        } catch (Exception e) {
            LOGGER.info(e.getMessage());
        }
    }

}

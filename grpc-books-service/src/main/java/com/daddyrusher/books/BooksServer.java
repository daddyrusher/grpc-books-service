package com.daddyrusher.books;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BooksServer {
    private static final Logger LOGGER = Logger.getLogger(BooksServer.class.getName());
    private static Books.BookList.Builder bookList;

    private Server server;

    static {
        bookList = Books.BookList.newBuilder();
    }

    public static void main(String[] args) throws InterruptedException {
        final BooksServer server = new BooksServer();
        server.start();
        server.blockUntilShutdown();
    }

    private void start() {
        int port = 50051;
        try {
            server = ServerBuilder.forPort(port)
                    .addService(new BookServiceImpl())
                    .build()
                    .start();

            LOGGER.info("Server started, listening on " + port);
        } catch (IOException e) {
            LOGGER.info("Server can't start");
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    static class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {
        public void list(Books.Empty request, StreamObserver<Books.BookList> responseObserver) {
            LOGGER.info("booklist elements: " + bookList.getBooksList());
            responseObserver.onNext(bookList.buildPartial());
            responseObserver.onCompleted();
        }

        public void insert(Books.Book request, StreamObserver<Books.Empty> responseObserver) {
           bookList.addAllBooks(new ArrayList<>() {{
               add(request);
           }}).build();
            LOGGER.info("booklist elems: " + bookList.getBooksList());
            responseObserver.onNext(Books.Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
    }
}

package com.ripplesolutions.server;

import com.google.gson.Gson;
import com.ripplesolutions.nfc.CardOptions;
import com.ripplesolutions.nfc.NFC;
import com.ripplesolutions.utils.Utils;
import javafx.beans.property.SimpleBooleanProperty;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class AsyncServlet extends HttpServlet {
    private static final String HEAVY_RESOURCE = "async task complete";
    private final SimpleBooleanProperty runningProperty = new SimpleBooleanProperty(false);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        run(true);
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        String customerId = request.getParameter("customerId");
        String operation = request.getParameter("operation");
        String overwrite = request.getParameter("overwrite");
        CardOptions options = new CardOptions(customerId, operation);
        AsyncContext async = request.startAsync();
        ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                if (out.isReady()) {
                    if (customerId == null || operation == null) {
                        throw new IOException("unsupported card parameters, contact admin");
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                    try {
                        String tagId = checkReaderStatus();
                        if (options.isRead()) {
                            options.setCustomerId(tagId);
                            boolean cardHasId = options.hasId();
                            if (!cardHasId) {
                                throw new IOException("(E002) Card is not registered, register card and try again");
                            }
                        } else {
                            boolean idWasSent = options.hasId();
                            if (!idWasSent) {
                                options.setCustomerId("");
                                throw new IOException("Customer ID unknown, save customer and try again");
                            } else {
                                CardOptions writeOptions = new CardOptions(tagId, operation);
                                boolean cardAlreadyRegistered = writeOptions.hasId();
                                if (cardAlreadyRegistered) {
                                    // check if in-coming id matches registered id
                                    if (!options.isSimilarTo(tagId)) {
                                        if (overwrite.equalsIgnoreCase("TRUE")) {
                                            boolean idWasWritten = Utils.setCustomerId(options.getCustomerId());
                                            if (!idWasWritten) {
                                                throw new IOException("Customer registration failed");
                                            }
                                        } else {
                                            throw new IOException("(E001) Card is already registered to someone else");
                                        }
                                    }
                                } else {
                                    boolean idWasWritten = Utils.setCustomerId(options.getCustomerId());
                                    if (!idWasWritten) {
                                        throw new IOException("Customer registration failed");
                                    }
                                }
                            }
                        }
                        Gson jsonResponse = new Gson();
                        String outPut = jsonResponse.toJson(options.getCustomerId());
                        run(false);
                        async.complete();
                        out.write(outPut.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        throw new IOException(e.getMessage());
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("System Error", t);
                run(false);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND, t.getMessage());
                async.complete();
            }
        });
    }

    private String checkReaderStatus() throws IOException {
        String tagId = "";
        try {
            tagId = Utils.getCustomerId();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        boolean readerPresent = NFC.readerPluggedIn;
        if (!readerPresent) {
            throw new IOException("Connect Card Reader and Try again");
        }
        boolean cardPresent = NFC.cardInRange;
        if (!cardPresent) {
            throw new IOException("Place Loyalty Card on Card Reader and Try again");
        }
        return tagId;
    }

    private void run(boolean value) {
        if (runningProperty != null) {
            runningProperty.setValue(value);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        run(true);
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        Collection<Part> parts = request.getParts();
        Gson jsonRequest = new Gson();
        CardOptions responseOptions = new CardOptions();
        Gson jsonResponse = new Gson();
        String outPut = jsonResponse.toJson(responseOptions);
        ByteBuffer content = ByteBuffer.wrap(outPut.getBytes(StandardCharsets.UTF_8));

        AsyncContext async = request.startAsync();
        ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() throws IOException {
                while (out.isReady()) {
                    if (!content.hasRemaining()) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        run(false);
                        async.complete();
                        return;
                    }
                    out.write(content.get());
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("System Error", t);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND, t.getMessage());
                run(false);
                async.complete();
            }
        });
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        run(true);
        response.setContentType("application/json");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");
        AsyncContext async = request.startAsync();
        ServletOutputStream out = response.getOutputStream();
        out.setWriteListener(new WriteListener() {
            @Override
            public void onWritePossible() {
                if (out.isReady()) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    run(false);
                    async.complete();
                }
            }

            @Override
            public void onError(Throwable t) {
                getServletContext().log("System Error", t);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND, t.getMessage());
                run(false);
                async.complete();
            }
        });
    }
}
package org.acme;

import org.jboss.logmanager.ExtHandler;
import org.jboss.logmanager.ExtLogRecord;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class LogCaptor implements AutoCloseable {
    private final InMemoryLogHandler handler;
    private final Logger logger;

    public LogCaptor(String loggerName) {
        handler = new InMemoryLogHandler();
        logger = LogManager.getLogManager().getLogger(loggerName);
        logger.addHandler(handler);
    }

    public BlockingQueue<ExtLogRecord> getRecords() {
        return handler.records;
    }

    @Override
    public void close() {
        try (
            UncheckedAutoCloseable a = () -> logger.removeHandler(handler);
            handler
        ) {
            // do nothing
        }
    }

    private interface UncheckedAutoCloseable extends AutoCloseable {
        @Override
        void close();
    }

    private static final class InMemoryLogHandler extends ExtHandler {
        private final BlockingQueue<ExtLogRecord> records = new LinkedBlockingQueue<>();

        @Override
        protected void doPublish(ExtLogRecord record) {
            records.add(record);
        }
    }
}

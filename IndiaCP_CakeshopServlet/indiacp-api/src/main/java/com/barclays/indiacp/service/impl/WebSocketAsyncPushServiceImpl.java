package com.barclays.indiacp.service.impl;

import com.barclays.indiacp.error.APIException;
import com.barclays.indiacp.model.APIError;
import com.barclays.indiacp.model.APIResponse;
import com.barclays.indiacp.model.Transaction;
import com.barclays.indiacp.service.TransactionService;
import com.barclays.indiacp.service.WebSocketAsyncPushService;
import com.barclays.indiacp.service.WebSocketPushService;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 *
 * @author Michael Kazansky
 */
@Component
public class WebSocketAsyncPushServiceImpl implements WebSocketAsyncPushService {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(WebSocketAsyncPushServiceImpl.class);

    @Autowired
    private TransactionService transactionService;

    @Override
    @Async
    public void pushTransactionAsync(final String transactionAddress, final SimpMessagingTemplate template,
            Map<String, Integer> transactions) {

        try {
            Transaction transaction = transactionService.get(transactionAddress);

            if (transaction == null || StringUtils.isBlank(transaction.getId())) {
                APIResponse apiResponse = new APIResponse();
                APIError err = new APIError();
                err.setStatus("404");
                err.setTitle("Transaction not found");
                apiResponse.addError(err);
                if (null != transactions && !transactions.isEmpty()) {
                    transactions.remove(transactionAddress);
                }
                template.convertAndSend(WebSocketPushService.TRANSACTION_TOPIC + transactionAddress, apiResponse);

            } else if (transaction.getStatus() == Transaction.Status.committed) {
                APIResponse apiResponse = new APIResponse();
                apiResponse.setData(transaction.toAPIData());
                if (null != transactions && !transactions.isEmpty()) {
                    transactions.remove(transactionAddress);
                }
                template.convertAndSend(WebSocketPushService.TRANSACTION_TOPIC + transactionAddress, apiResponse);
            }

        } catch (APIException ex) {
            LOG.error(ex.getMessage());
        }
    }

}

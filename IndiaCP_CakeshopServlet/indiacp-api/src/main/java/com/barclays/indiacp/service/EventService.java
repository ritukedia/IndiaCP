package com.barclays.indiacp.service;

import com.barclays.indiacp.error.APIException;
import com.barclays.indiacp.model.Event;

import java.io.IOException;

import java.util.List;
import java.util.Map;

public interface EventService {

    public List<Event> listForBlock(Long blockNumber) throws APIException;

    List<Event> processEvents(List<Map<String, Object>> directEvents) throws APIException;
    public String serialize(Object obj) throws IOException;
    public Object deserialize(String data) throws IOException, ClassNotFoundException;


}

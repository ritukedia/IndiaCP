package com.barclays.indiacp.service.impl;

import com.barclays.indiacp.model.QuorumInfo;
import com.barclays.indiacp.service.GethHttpService;
import com.barclays.indiacp.service.QuorumService;
import com.barclays.indiacp.bean.GethConfigBean;
import com.barclays.indiacp.error.APIException;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuorumServiceImpl implements QuorumService {

    @Autowired
    private GethConfigBean gethConfig;

    @Autowired
    private GethHttpService geth;

    @Override
    public boolean isQuorum() {
        if (gethConfig.isQuorum() == null) {
            try {
                getQuorumInfo();
                gethConfig.setQuorum(true);

            } catch (APIException e) {
                if (e.getMessage().contains("method quorum_nodeInfo does not exist")) {
                    gethConfig.setQuorum(false);
                }
            }
        }

        if (gethConfig.isQuorum() != null) {
            return gethConfig.isQuorum();
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public QuorumInfo getQuorumInfo() throws APIException {

        Map<String, Object> data = geth.executeGethCall("quorum_nodeInfo");

        QuorumInfo info = new QuorumInfo();
        info.setQuorum(true);

        if (data == null || data.isEmpty()) {
            return info;
        }

        info.setBlockMakerAccount((String) data.get("blockMakerAccount"));
        info.setVoteAccount((String) data.get("voteAccount"));
        info.setCanCreateBlocks((Boolean) data.get("canCreateBlocks"));
        info.setCanVote((Boolean) data.get("canVote"));

        Map<String, Object> strat = (Map<String, Object>) data.get("blockmakestrategy");
        if (strat != null && !strat.isEmpty()) {
            QuorumInfo.BlockMakerStrategy bstrat = new QuorumInfo.BlockMakerStrategy();
            bstrat.setMinBlockTime((int) strat.get("minblocktime"));
            bstrat.setMaxBlockTime((int) strat.get("maxblocktime"));
            bstrat.setStatus((String) strat.get("status"));
            bstrat.setType((String) strat.get("type"));
            info.setBlockMakerStrategy(bstrat);
        }

        return info;
    }

}

package cn.InstFS.wkr.NetworkMining.Miner.Algorithms;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.MinerType;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningMethod;
import cn.InstFS.wkr.NetworkMining.TaskConfigure.TaskRange;

import lineAssociation.FileOutput;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Author: arbor
 * Date: 16-12-16.
 */
public class AlgorithmsManager {

    private AlgorithmsChooser singleNodeAlgoChooser;
    private AlgorithmsChooser nodePairAlgoChooser;

    private static class AlgorithmsManagerHolder {
        private static final AlgorithmsManager INSTANCE = new AlgorithmsManager();
    }

    public static final AlgorithmsManager getInstance() {
        return AlgorithmsManagerHolder.INSTANCE;
    }

    private AlgorithmsManager() {

    }

    public AlgorithmsChooser getAlgoChooserFromManager(MinerType minerType, TaskRange taskRange) {
        switch (minerType) {
            case MiningType_SinglenodeOrNodePair:
                if (taskRange.equals(TaskRange.SingleNodeRange)) {
                    if (singleNodeAlgoChooser == null) {
                        singleNodeAlgoChooser = new AlgorithmsChooser();
                        Element methodListElement = getMethodListElement(minerType, taskRange);
                        setAlgoToChooser(singleNodeAlgoChooser, methodListElement);
                    }
                    return singleNodeAlgoChooser;
                } else if (taskRange.equals(TaskRange.NodePairRange)){
                    if (nodePairAlgoChooser == null) {
                        nodePairAlgoChooser = new AlgorithmsChooser();
                        Element methodListElement = getMethodListElement(minerType, taskRange);
                        setAlgoToChooser(nodePairAlgoChooser, methodListElement);
                    }
                    return nodePairAlgoChooser;
                }
                break;
            case MiningTypes_WholeNetwork:
                break;
            default:
                break;
        }

        return null;
    }

    private Element getMethodListElement(MinerType minerType, TaskRange taskRange) {
        List<Element> classElementList;
        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            File inputFile = new File("configs/algorithmsManager.xml");
            Document document = saxBuilder.build(inputFile);

            classElementList = document.getRootElement().getChildren();
            for (Element e: classElementList) {
                if (e.getAttributeValue("type").equals(minerType.toString())) {
                    if (minerType.equals(MinerType.MiningType_SinglenodeOrNodePair)) {
                        List<Element> eRangeList = e.getChildren();
                        for (Element eRange: eRangeList) {
                            if (eRange.getAttributeValue("range").equals(taskRange.name())) {
                                return eRange;
                            }
                        }
                    }

                    return e;
                }
            }
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setAlgoToChooser(AlgorithmsChooser chooser, Element methodListElement) {
        for (Element e : (List<Element>)methodListElement.getChildren()) {
            String method = e.getAttributeValue("method");
            String flag = e.getChild("custom").getAttributeValue("flag");
            MiningAlgo algo = MiningAlgo.fromString(e.getChild("custom").getChildText("miningAlgo"));

            if (flag.equals("true")) {
                switch (MiningMethod.fromString(method)) {
                    case MiningMethods_PeriodicityMining:
                        chooser.setPmAlgo(algo);
                        break;
                    case MiningMethods_OutliesMining:
                        chooser.setOmAlgo(algo);
                        break;
                    case MiningMethods_PredictionMining:
                        chooser.setFmAlgo(algo);
                        break;
                }
            }
        }
    }

    public static void main(String args[]) {
        AlgorithmsChooser chooser = AlgorithmsManager.getInstance().getAlgoChooserFromManager(MinerType.MiningType_SinglenodeOrNodePair, TaskRange.SingleNodeRange);
        chooser.getPmAlgo();
    }
}

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
    private AlgorithmsChooser proAssAlgoChooser;
    private AlgorithmsChooser networkAlgoChooser;
    private AlgorithmsChooser pathAlgoChooser;

    private static class AlgorithmsManagerHolder {
        private static final AlgorithmsManager INSTANCE = new AlgorithmsManager();
    }

    public static final AlgorithmsManager getInstance() {
        return AlgorithmsManagerHolder.INSTANCE;
    }

    private AlgorithmsManager() {

    }

    public AlgorithmsChooser getAlgoChooserFromManager(MinerType minerType, TaskRange taskRange) {
        AlgorithmsChooser chooser = null;
        switch (minerType) {
            case MiningType_SinglenodeOrNodePair:
                if (taskRange.equals(TaskRange.SingleNodeRange)) {
                    if (singleNodeAlgoChooser == null) {
                        singleNodeAlgoChooser = new AlgorithmsChooser();
                        chooser = singleNodeAlgoChooser;
                    } else
                        return singleNodeAlgoChooser;
                } else if (taskRange.equals(TaskRange.NodePairRange)){
                    if (nodePairAlgoChooser == null) {
                        nodePairAlgoChooser = new AlgorithmsChooser();
                        chooser = nodePairAlgoChooser;
                    } else
                        return nodePairAlgoChooser;
                }
                break;
            case MiningTypes_WholeNetwork:
                if (networkAlgoChooser == null) {
                    networkAlgoChooser = new AlgorithmsChooser();
                    chooser = networkAlgoChooser;
                } else
                    return networkAlgoChooser;
                break;
            case MiningType_ProtocolAssociation:
                if (proAssAlgoChooser == null) {
                    proAssAlgoChooser = new AlgorithmsChooser();
                    chooser = proAssAlgoChooser;
                } else
                    return proAssAlgoChooser;
                break;
            case MiningType_Path:
                if (pathAlgoChooser == null) {
                    pathAlgoChooser = new AlgorithmsChooser();
                    chooser = pathAlgoChooser;
                } else
                    return pathAlgoChooser;
                break;
            default:
                throw new RuntimeException("不存在该挖掘类型");
        }
        Element methodListElement = getMethodListElement(minerType, taskRange);
        setAlgoToChooser(chooser, methodListElement);
        return chooser;
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
                    case MiningMethods_FrequenceItemMining:
                        chooser.setProAssAlgo(algo);
                        break;
                    case MiningMethods_SimilarityMining:
                        chooser.setSimAlgo(algo);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void resetAlgorithmsChooser() {
        singleNodeAlgoChooser = null;
        nodePairAlgoChooser = null;
        proAssAlgoChooser = null;
        networkAlgoChooser = null;
        pathAlgoChooser = null;
    }

    public static void main(String args[]) {
        AlgorithmsChooser chooser = AlgorithmsManager.getInstance().getAlgoChooserFromManager(MinerType.MiningType_SinglenodeOrNodePair, TaskRange.SingleNodeRange);
        chooser.getPmAlgo();
    }
}

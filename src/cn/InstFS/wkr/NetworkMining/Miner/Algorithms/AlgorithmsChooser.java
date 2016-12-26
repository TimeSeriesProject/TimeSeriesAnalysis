package cn.InstFS.wkr.NetworkMining.Miner.Algorithms;

import cn.InstFS.wkr.NetworkMining.TaskConfigure.MiningAlgo;

/**
 * Author: arbor
 * Date: 16-12-16.
 */
public class AlgorithmsChooser {
    private MiningAlgo pmAlgo;  // 周期
    private MiningAlgo omAlgo;  // 异常
    private MiningAlgo fmAlgo;  // 预测
    private MiningAlgo simAlgo; // 多元序列相似度
    private MiningAlgo proAssAlgo; // 多元序列关联

    public MiningAlgo getPmAlgo() {
        return pmAlgo;
    }

    public void setPmAlgo(MiningAlgo pmAlgo) {
        this.pmAlgo = pmAlgo;
    }

    public MiningAlgo getOmAlgo() {
        return omAlgo;
    }

    public void setOmAlgo(MiningAlgo omAlgo) {
        this.omAlgo = omAlgo;
    }

    public MiningAlgo getFmAlgo() {
        return fmAlgo;
    }

    public void setFmAlgo(MiningAlgo fmAlgo) {
        this.fmAlgo = fmAlgo;
    }

    public MiningAlgo getSimAlgo() {
        return simAlgo;
    }

    public void setSimAlgo(MiningAlgo simAlgo) {
        this.simAlgo = simAlgo;
    }

    public MiningAlgo getProAssAlgo() {
        return proAssAlgo;
    }

    public void setProAssAlgo(MiningAlgo proAssAlgo) {
        this.proAssAlgo = proAssAlgo;
    }
}

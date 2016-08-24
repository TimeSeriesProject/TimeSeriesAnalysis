package cn.InstFS.wkr.NetworkMining.DataInputs;

import java.util.ArrayList;
import java.util.List;


public class GMMParameter {
	private ArrayList<ArrayList<Double>> pMiu; // 均值参数k个分布的中心点，每个中心点d维 k*d
	private ArrayList<Double> pPi; // k个GMM的权值 k*d
	private ArrayList<ArrayList<Double>> pSigma; // k类GMM的协方差矩阵,k*d 
	public ArrayList<ArrayList<Double>> getpMiu() {
		return pMiu;
	}
	public void setpMiu(ArrayList<ArrayList<Double>> pMiu) {
		this.pMiu = pMiu;
	}
	public ArrayList<Double> getpPi() {
		return pPi;
	}
	public void setpPi(ArrayList<Double> pPi) {
		this.pPi = pPi;
	}
	public ArrayList<ArrayList<Double>> getpSigma() {
		return pSigma;
	}
	public void setpSigma(ArrayList<ArrayList<Double>> pSigma) {
		this.pSigma = pSigma;
	}

}

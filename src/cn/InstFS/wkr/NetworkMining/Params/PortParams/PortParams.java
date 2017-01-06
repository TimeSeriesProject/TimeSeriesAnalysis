package cn.InstFS.wkr.NetworkMining.Params.PortParams;

import org.jdom.Element;

import java.util.HashSet;
import java.util.List;

/**
 * Created by zsc on 2016/11/29.
 */
public class PortParams {
    private HashSet<Integer> ports = new HashSet<Integer>();

    public PortParams(Element element) {
        List<Element> list = element.getChildren();

        for (int i = 0; i < list.size(); i++) {
            ports.add(Integer.parseInt(list.get(i).getText()));
        }
    }

    public HashSet<Integer> getPorts() {
        return ports;
    }

    public void setPorts(HashSet<Integer> ports) {
        this.ports = ports;
    }
}

package com.example.demo.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;

public class ListDG {
    // 邻接表中表对应的链表的顶点
    @Data
    private class ENode {
        int ivex; // 该边所指向的顶点的位置
        String data;
    }

    // 邻接表中表的顶点
    @Data
    private class VNode {
        String data; // 顶点信息
        List<ENode> inDegrees;
    };

    private VNode[] mVexs; // 顶点数组

    private String[] vexs; // 顶点数组

    private String[][] edges; // 边数组
    /*
     * 创建图(用已提供的矩阵)
     *
     * 参数说明： vexs -- 顶点数组 edges -- 边数组
     */

    public ListDG(String[] vexs, String[][] edges) {
        this.vexs = vexs;
        this.edges = edges;

        // 初始化"顶点数"和"边数"
        int vlen = vexs.length;
        int elen = edges.length;

        // 初始化"顶点"
        mVexs = new VNode[vlen];
        for (int i = 0; i < mVexs.length; i++) {
            mVexs[i] = new VNode();
            mVexs[i].data = vexs[i];
            mVexs[i].inDegrees = new ArrayList<>();
        }

        // 初始化"边"
        for (int i = 0; i < elen; i++) {
            // 读取边的起始顶点和结束顶点
            String c1 = edges[i][0];
            String c2 = edges[i][1];
            // 读取边的起始顶点和结束顶点
            int p1 = getPosition(c1);
            int p2 = getPosition(c2);

            // 初始化node1
            ENode node1 = new ENode();
            node1.ivex = p2;
            node1.data = mVexs[p2].data;
            // 将node1链接到"p1所在链表的末尾"
            mVexs[p1].inDegrees.add(node1);
        }
    }

    /*
     * 返回ch位置
     */
    private int getPosition(String ch) {
        for (int i = 0; i < mVexs.length; i++)
            if (mVexs[i].data.equals(ch))
                return i;
        return -1;
    }

    /*
     * 打印矩阵队列图
     */
    public void print() {
        System.out.printf("List Graph:\n");
        for (int i = 0; i < mVexs.length; i++) {
            System.out.printf("%d(%s): ", i, mVexs[i].data);
            List<ENode> nodes = mVexs[i].inDegrees;
            for (ENode node : nodes) {
                System.out.printf("%d(%s)", node.ivex, node.data);
            }

            System.out.printf("\n");
        }
    }

    static void getVexsAndEdgesFromMetrics(Metrics m, HashSet<String> vexs, HashSet<String[]> edges,
            HashMap<String, Metrics> map) {
        vexs.add(m.getId());
        map.remove(m.getId());
        if (!m.getParams().isEmpty()) {
            for (String s : m.getParams()) {
                String[] newEdge = new String[] {m.getId(), s};
                edges.add(newEdge);
                Metrics pm = map.get(s);
                if (pm != null) {
                    getVexsAndEdgesFromMetrics(pm, vexs, edges, map);
                }

            }
        }
    }

    public static void main(String[] args) {
        ArrayList<String> aParams = new ArrayList<>();
        Metrics a = new Metrics("A", "SUM(code1)", aParams);

        ArrayList<String> bParams = new ArrayList<>();
        Metrics b = new Metrics("B", "COUNT(code1)", bParams);

        ArrayList<String> cParams = new ArrayList<>();
        cParams.add(a.getId());
        cParams.add(b.getId());
        Metrics c = new Metrics("C", "A + B", cParams);

        ArrayList<String> dParams = new ArrayList<>();
        dParams.add(b.getId());
        Metrics d = new Metrics("D", "B + 1", dParams);

        ArrayList<String> eParams = new ArrayList<>();
        eParams.add(c.getId());
        eParams.add(d.getId());
        Metrics e = new Metrics("E", "C + D", eParams);

        HashMap<String, Metrics> map = new HashMap<>();
        map.put(a.getId(), a);
        map.put(b.getId(), b);
        map.put(c.getId(), c);
        map.put(d.getId(), d);
        map.put(e.getId(), e);

        HashSet<String> v = new HashSet<>();
        HashSet<String[]> ed = new HashSet<>();

        getVexsAndEdgesFromMetrics(e, v, ed, map);

        String[] vexs = v.toArray(new String[v.size()]);
        String[][] edges = ed.toArray(new String[2][ed.size()]);
        ListDG pG;

        // 采用已有的"图"
        pG = new ListDG(vexs, edges);

        pG.print(); // 打印图

        List<String> topo = pG.getTopoSort();
        if (topo.isEmpty()) {
            System.out.printf("有环");
        } else {
            System.out.printf("拓扑排序：");
            for (String s : topo) {
                System.out.printf(s);
            }
        }

        System.out.printf("\n");


        ArrayList<String> d1Params = new ArrayList<>();
        d1Params.add(e.getId());
        Metrics d1 = new Metrics("D", "E + 1", d1Params);
        
        HashMap<String, Metrics> map1 = new HashMap<>();
        map1.put(a.getId(), a);
        map1.put(b.getId(), b);
        map1.put(c.getId(), c);
        map1.put(d1.getId(), d1);
        map1.put(e.getId(), e);

        HashSet<String> v1 = new HashSet<>();
        HashSet<String[]> ed1 = new HashSet<>();

        getVexsAndEdgesFromMetrics(e, v1, ed1, map1);

        String[] vexs1 = v1.toArray(new String[v1.size()]);
        String[][] edges1 = ed1.toArray(new String[2][ed1.size()]);
        ListDG pG1;

        // 采用已有的"图"
        pG1 = new ListDG(vexs1, edges1);

        pG1.print(); // 打印图

        List<String> topo1 = pG1.getTopoSort();
        if (topo1.isEmpty()) {
            System.out.printf("有环");
        } else {
            System.out.printf("拓扑排序：");
            for (String s : topo1) {
                System.out.printf(s);
            }
        }
    }

    private List<String> getTopoSort() {
        List<String> list = new ArrayList<>();
        List<VNode> q = new ArrayList<>();

        List<VNode> l = new ArrayList<>();
        // 拷贝顶点数组
        for (int i = 0; i < mVexs.length; i++) {
            l.add(mVexs[i]);
        }

        //将入度为0的顶点加入队列
        for (int i = 0; i < l.size(); i++) {
            VNode v = l.get(i);
            if (v.getInDegrees().isEmpty()) {
                q.add(v);
            }
        }

        while (!q.isEmpty()) {
            //从顶点数组中删除入度为0的顶点
            for (VNode v : q) {
                l.remove(v);
            }

            for (VNode v : q) {
                //将入度为0的顶点加入拓扑排序
                list.add(v.getData());
                for (VNode n : l) {
                    List<ENode> outDegrees = n.getInDegrees();
                    //从剩余顶点的入度中删除入度为0的顶点
                    n.setInDegrees(
                            outDegrees.stream().filter(o -> !o.getData().equals(v.getData()))
                                    .collect(Collectors.toList()));
                }
            }

            q = new ArrayList<>();
            //将入度为0的顶点加入队列
            for (int i = 0; i < l.size(); i++) {
                VNode v = l.get(i);
                if (v.getInDegrees().isEmpty()) {
                    q.add(v);
                }
            }
        }

        return list.size() == mVexs.length ? list : new ArrayList<>();
    }

}

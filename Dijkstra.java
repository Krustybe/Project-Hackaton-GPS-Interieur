package com.example.myapplication;

import java.util.*;
import java.util.Arrays;

public class Dijkstra {

    private static class Node implements Comparable<Node> {
        int distance;
        String vertex;

        public Node(int distance, String vertex) {
            this.distance = distance;
            this.vertex = vertex;
        }

        @Override
        public int compareTo(Node other) {
            return Integer.compare(this.distance, other.distance);
        }
    }

    private static Map<String, List<Object[]>> carto = new HashMap<>();
    private static Map<String, String> mapping = new HashMap<>();

    static {
        initializeCarto();
        initializeMapping();
    }

    private static void initializeCarto() {

        //Ecriture du tableau Carto
        Object[][][] tableau_Carto_Hacka = new Object[][][]{
                {{"1","2","EST","2","EST"}},
                {{"2","3","SUD","6","NORD","1","OUEST"}},
                {{"3","2","NORD","4","OUEST","5","EST"}},
                {{"4","3","EST","3","EST"}},
                {{"5","3","OUEST","16","ESCALIER"}},
                {{"6","2","SUD","7","OUEST","8","EST"}},
                {{"7","6","EST","6","EST"}},
                {{"8","6","OUEST","9","ESCALIER"}},
                {{"9","10","OUEST","8","ESCALIER"}},
                {{"10","11","OUEST","9","EST","12","SUD"}},
                {{"11","10","EST","10","EST"}},
                {{"12","10","NORD","13","OUEST","14","SUD"}},
                {{"13","12","EST","12","EST"}},
                {{"14","12","NORD","15","OUEST","16","EST"}},
                {{"15","14","EST","14","EST"}},
                {{"16","14","OUEST","5","ESCALIER"}}
        };

        //Carto Batiment C
        Object[][][] tableau_Carto_BAT_C = new Object[][][]{
                {{"1","2","NORD","9","ESCALIER","9","ASCENSEUR","13","ASCENSEUR","17","ASCENSEUR"}},
                {{"2","1","SUD","3","OUEST","8","NORD"}},
                {{"3","2","EST","4","OUEST"}},
                {{"4","3","EST","5","NORD","12","ESCALIER"}},
                {{"5","4","SUD","6","NORD"}},
                {{"6","5","SUD","7","EST","8"}},
                {{"7","6","OUEST","21","EST"}},
                {{"8","2","SUD","21","NORD"}},
                {{"9","10","NORD","1","ESCALIER","13","ESCALIER","1","ASCENSEUR","13","ASCENSEUR","17","ASCENSEUR"}},
                {{"10","9","SUD","11","NORD","12","OUEST"}},
                {{"11","10","SUD","10","SUD"}},
                {{"12","10","OUEST","16","ESCALIER","4","ESCALIER"}},
                {{"13","14","NORD","9","ESCALIER","20","ESCALIER","1","ASCENSEUR","9","ASCENSEUR","17","ASCENSEUR"}},
                {{"14","13","SUD","15","NORD","16","OUEST"}},
                {{"15","14","SUD","14","SUD"}},
                {{"16","14","EST","12","ESCALIER","20","ESCALIER MONTANT"}},
                {{"17","18","NORD","13","ESCALIER","1","ASCENSEUR","13","ASCENSEUR","9","ASCENSEUR"}},
                {{"18","17","SUD","19","NORD","20","OUEST"}},
                {{"19","18","SUD","18","SUD"}},
                {{"20","18","EST","16","ESCALIER"}}
        };

        // Initialize carto using tableau_Carto
        for (Object[][] obj : tableau_Carto_BAT_C) {
            String key = (String) obj[0][0];
            List<Object[]> connections = new ArrayList<>();
            for (int i = 1; i < obj[0].length; i += 6) {
                connections.add(new Object[]{3, obj[0][i], obj[0][i + 1]});
            }
            carto.put(key, connections);
        }

        // Ajouter un cas pour "H"
        carto.put("H", Collections.emptyList());

    }

    private static void initializeMapping() {

        //Création Tableau mapping
        Object[][][] tableau_Mapping_Hacka = new Object[][][]{
                {{1,"ENTREE"}},
                {{3,"B10","B03","B04"}},
                {{4,"B001","B002","B009","B008"}},
                {{5,"B006","B007"}},
                {{6,"A003","A005"}},
                {{7,"A001","A002"}},
                {{8,"A006","A007"}},
                {{9,"A106","A107"}},
                {{10,"A103","A105"}},
                {{11,"A101","A102"}},
                {{14,"B103","B105","B110"}},
                {{15,"B101","B102","B108","B109"}},
                {{16,"B106","B107"}}
        };

        //Mapping bat C
        Object[][][] tableau_Mapping_BAT_C= new Object[][][]{
                {{1,"JARDIN"}},
                {{3,"TOILETTE 0","BDLS","ARENA","EPICURIA","RESERVE DES ASSOS","SALLE PROF"}},
                {{5,"DOUCHE GARCON","DOUCHE FILLE","MADEIN"}},
                {{7,"QUANTUM","BDA","TYO","WAVE"}},
                {{8,"MUSCU"}},
                {{9,"PASSERELLE BATIMENT D"}},
                {{11,"PAR VIE 1ER"}},
                {{12,"C101","C102","C103","C104","C105","C106"}},
                {{13,"C207"}},
                {{15,"C208"}},
                {{16,"C201","C202","C203","C204","C205","C206"}},
                {{17,"C307"}},
                {{19,"C308"}},
                {{20,"C301","C302","C303","C304","C305","C306"}}
        };

        // Initialize mapping using tableau_Mapping
        for (Object[][] mapEntry : tableau_Mapping_BAT_C) {
            int key = (int) mapEntry[0][0];
            for (int i = 1; i < mapEntry[0].length; i++) {
                String value = (String) mapEntry[0][i];
                mapping.put(value, String.valueOf(key));
            }
        }
    }

    public static List<String> dijkstra(String depart, String adr) {
        String t = mapping.get(adr);
        String s = mapping.get(depart);
        Set<String> M = new HashSet<>();
        Map<String, Integer> d = new HashMap<>();
        Map<String, String> p = new HashMap<>();
        Map<String, String> c = new HashMap<>();

        d.put(s, 0);

        PriorityQueue<Node> suivants = new PriorityQueue<>();
        suivants.add(new Node(0, s));

        while (!suivants.isEmpty()) {
            Node current = suivants.poll();
            int dx = current.distance;
            String x = current.vertex;

            if (M.contains(x)) {
                continue;
            }

            M.add(x);

            List<Object[]> neighbors = carto.get(x);
            if (neighbors != null) {
                for (Object[] neighbor : neighbors) {
                    int w = (int) neighbor[0];
                    String y = (String) neighbor[1];
                    String z = (String) neighbor[2];

                    if (M.contains(y)) {
                        continue;
                    }

                    int dy = dx + w;

                    if (!d.containsKey(y) || d.get(y) > dy) {
                        d.put(y, dy);
                        suivants.add(new Node(dy, y));
                        p.put(y, x);
                        c.put(y, z);
                    }
                }
            }
        }

        List<String> path = new ArrayList<>();
        String x = t;
        while (x != null && !x.equals(s)) {
            path.add(0, c.get(x));
            x = p.get(x);
        }

        return path;
    }

    public static List<Object[]> voisins(String s) {
        return carto.get(s);
    }
}
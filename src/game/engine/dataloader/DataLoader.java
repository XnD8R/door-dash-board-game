package game.engine.dataloader;

import java.io.BufferedReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import game.engine.Role;
import game.engine.cards.*;
import game.engine.cells.*;
import game.engine.exceptions.InvalidCSVFormat;
import game.engine.monsters.*;

public class DataLoader {

    public static final String CARDS_FILE_NAME = "cards.csv";
    public static final String CELLS_FILE_NAME = "cells.csv";
    private static final String MONSTERS_FILE_NAME = "monsters.csv";  

    public static ArrayList<Card> readCards() throws IOException {
        ArrayList<Card> cards = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(CARDS_FILE_NAME));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String type = parts[0].trim().toUpperCase();
            String name = parts[1].trim();
            String description = parts[2].trim();
            int rarity = Integer.parseInt(parts[3].trim());

            switch (type) { 
            case "SWAPPER":
                cards.add(new SwapperCard(name, description, rarity));
                break;
            case "SHIELD":
                cards.add(new ShieldCard(name, description, rarity));
                break;
            case "ENERGYSTEAL":
                int energy = Integer.parseInt(parts[4].trim());
                cards.add(new EnergyStealCard(name, description, rarity, energy));
                break;
            case "STARTOVER":
                boolean lucky = Boolean.parseBoolean(parts[4].trim());
                cards.add(new StartOverCard(name, description, rarity, lucky));
                break;
            case "CONFUSION":
                int duration = Integer.parseInt(parts[4].trim());
                cards.add(new ConfusionCard(name, description, rarity, duration));
                break;
            default:
                throw new InvalidCSVFormat(line);
        }
        }
        reader.close();
        return cards;
    }

    public static ArrayList<Cell> readCells() throws IOException {
        ArrayList<Cell> cells = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CELLS_FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; 

                String[] parts = line.split(",");

                try {
                    
                    Role role   = Role.valueOf(parts[1].trim());
                    int  energy = Integer.parseInt(parts[2].trim());
                    cells.add(new DoorCell(parts[0].trim(), role, energy));
                } catch (Exception e) {
                
                    int effect = Integer.parseInt(parts[1].trim());
                    if (effect >= 0) cells.add(new ConveyorBelt(parts[0].trim(), effect));
                    else             cells.add(new ContaminationSock(parts[0].trim(), effect));
                }
            }
        }
        return cells;
    }

    public static ArrayList<Monster> readMonsters() throws IOException {
        ArrayList<Monster> monsters = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(MONSTERS_FILE_NAME));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String type = parts[0].trim().toUpperCase();
            String name = parts[1].trim();
            String description = parts[2].trim();
            Role role = Role.valueOf(parts[3].trim().toUpperCase()); 
            System.out.println("Debug: loading " + name + " with role " + role);
            int energy = Integer.parseInt(parts[4].trim());

            switch (type) { 
            case "DASHER":
                monsters.add(new Dasher(name, description, role, energy));
                break;
            case "DYNAMO":
                monsters.add(new Dynamo(name, description, role, energy));
                break;
            case "MULTITASKER":
                monsters.add(new MultiTasker(name, description, role, energy));
                break;
            case "SCHEMER":
                monsters.add(new Schemer(name, description, role, energy));
                break;
            default:
                throw new InvalidCSVFormat(line);
        }
        }
        reader.close();
        return monsters;
    }
}

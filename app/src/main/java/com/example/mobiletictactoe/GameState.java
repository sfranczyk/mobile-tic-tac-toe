package com.example.mobiletictactoe;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameState {
    protected CellValue[] cellValues = new CellValue[100];
    protected CellValue currentTurn;
    protected final int[] dimensions = {10, 10};
    protected int numberToWin = 5;

    protected CellValue winner = CellValue.FREE;
    protected ArrayList<int[]> winLine = new ArrayList<>();

    public GameState() {
        reset(CellValue.O);
    }

    public GameState(CellValue firstPlayer) {
        reset(firstPlayer);
    }

    public CellValue getCellValue(int cellIndex) {
        if ( cellIndex < 0 || 100 <= cellIndex) {
            return CellValue.ERR;
        }
        return cellValues[cellIndex];
    }

    public void setCellValues(CellValue[] cellValues) {
        this.cellValues = cellValues;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setCellValues(int[] cellIntValues) {
        if (cellIntValues.length == this.cellValues.length) {
            for (int i = 0; i < cellIntValues.length; ++i) {
                cellValues[i] = cellIntValues[i] >= 0 && cellIntValues[i] < 4
                        ? CellValue.values()[cellIntValues[i]] : CellValue.ERR;
            }
        }
    }

    public boolean playerMove(int cellIndex) {
        if( isEnd() || cellValues[cellIndex] != CellValue.FREE ) {
            return false;
        }
        cellValues[cellIndex] = currentTurn;
        currentTurn = currentTurn == CellValue.O ? CellValue.X : CellValue.O;
        return true;
    }

    public boolean checkWin() {
        if (isEnd())
            return true;

        CellValue winner = CellValue.FREE;
        ArrayList<int[]> line = new ArrayList<>();
        
        //horisontal
        for (int row = 0; row < dimensions[0]; row++) {
            line.clear();
            for (int col = 0; col < dimensions[1]; col++) {
                winner = checkNextCell(col, row, winner, line);
                if (line.size() == numberToWin && winner != CellValue.FREE)
                    break;
            }
            if (line.size() == numberToWin)
                break;
        }

        //vertical
        if (line.size() != numberToWin)
            for (int col = 0; col < dimensions[0]; col++) {
                line.clear();
                for (int row = 0; row < dimensions[1]; row++) {
                    winner = checkNextCell(col, row, winner, line);
                    if (line.size() == numberToWin && winner != CellValue.FREE)
                        break;
                }
                if (line.size() == numberToWin)
                    break;
            }

        //slant left
        if (line.size() != numberToWin)
            for (int i = 0; i < (dimensions[0] - numberToWin + 1); i++) {
                line.clear();
                for (int j = 0; j < dimensions[1] && j + i < dimensions[0]; j++) {
                    winner = checkNextCell(j + i, j, winner, line);
                    if (line.size() == numberToWin && winner != CellValue.FREE)
                        break;
                }
                if (line.size() == numberToWin)
                    break;
            }
        if (line.size() != numberToWin)
            for (int i = 1; i < (dimensions[1] - numberToWin + 1); i++) {
                line.clear();
                for (int j = 0; j < dimensions[0] && j + i < dimensions[1]; j++) {
                    winner = checkNextCell(j, j + i, winner, line);
                    if (line.size() == numberToWin && winner != CellValue.FREE)
                        break;
                }
                if (line.size() == numberToWin)
                    break;
            }

        // slant right
        if (line.size() != numberToWin)
            for (int start = 0; start < (dimensions[1] - numberToWin + 1); start++) {
                line.clear();
                for (int i = dimensions[1] - 1 - start, j = 0; i >= 0; --i, ++j) {
                    winner = checkNextCell(j, i, winner, line);
                    if (line.size() == numberToWin && winner != CellValue.FREE)
                        break;
                }
                if (line.size() == numberToWin)
                    break;
            }
        if (line.size() != numberToWin)
            for (int start = 1; start < (dimensions[0] - numberToWin + 1); start++) {
                line.clear();
                for (int i = dimensions[1] - 1, j = start; j < dimensions[0]; --i, ++j) {
                    winner = checkNextCell(j, i, winner, line);
                    if (line.size() == numberToWin && winner != CellValue.FREE)
                        break;
                }
                if (line.size() == numberToWin)
                    break;
            }

        if (line.size() == numberToWin) {
            this.winner = winner;
            this.winLine = line;
            return true;
        }
        return false;
    }

    private CellValue checkNextCell(int col, int row, CellValue winner, ArrayList<int[]> line) {
        CellValue nextCell = getCellValue(col, row);
        if (nextCell != winner)
            line.clear();
        if (nextCell == CellValue.O || nextCell == CellValue.X)
            line.add(new int[]{col, row});
        return nextCell;
    }

    public int getIndex(int x, int y) {
        return x * dimensions[0] + y;
    }

    private CellValue getCellValue(int x, int y) {
        return cellValues[getIndex(x, y)];
    }

    public CellValue getWinner() {
        return winner;
    }

    public ArrayList<int[]> getWinLine() {
        return winLine;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Integer> getWinLineIndex() {
        return winLine.stream().map(d -> getIndex(d[0], d[1])).collect(Collectors.toList());
    }

    public boolean isEnd() {
        return winner != CellValue.FREE;
    }

    public void reset(CellValue firstPlayer) {
        for (int i = 0; i < dimensions[0] * dimensions[1]; i++) {
            cellValues[i] = CellValue.FREE;
        }
        winner = CellValue.FREE;
        winLine.clear();
        currentTurn = firstPlayer;
    }

    public ArrayList<Integer> getFreeCellIndexes() {
        ArrayList<Integer> freeCells = new ArrayList<>();
        for (int i = 0; i < cellValues.length; i++) {
            if (cellValues[i] == CellValue.FREE)
                freeCells.add(i);
        }
        return freeCells;
    }
}

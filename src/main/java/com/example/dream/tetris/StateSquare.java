package com.example.dream.tetris;

public class StateSquare {
    public static int[] color = new int[]{
            R.drawable.b, R.drawable.g, R.drawable.p, R.drawable.r, R.drawable.y};
    public static int[][] shape = new int[][]{
            {0x8, 0x8, 0xc, 0},
            {0xe, 0x8, 0, 0},
            {0xc, 0x4, 0x4, 0},
            {0x2, 0xe, 0x0, 0x0},

            {0x4, 0x4, 0xc, 0x0},
            {0x8, 0xe, 0x0, 0x0},
            {0xc, 0x8, 0x8, 0x0},
            {0xe, 0x2, 0x0, 0x0},
            {0x8, 0xc, 0x4, 0x0},
            {0x6, 0xc, 0x0, 0x0},
            {0x4, 0xc, 0x8, 0x0},
            {0xc, 0x6, 0x0, 0x0},
            {0x4, 0xe, 0x0, 0x0},
            {0x8, 0xc, 0x8, 0x0},
            {0xe, 0x4, 0x0, 0x0},
            {0x4, 0xc, 0x4, 0x0},

            {0x4, 0x4, 0x4, 0x4},
            {0x0, 0xf, 0x0, 0x0},
            {0xc, 0xc, 0x0, 0x0}
    };
    //19个方块对应19个初始位置
    public static int[][] initPosition = new int[][]
            {
                    {2, -2},
                    {3, -1},
                    {2, -2},
                    {3, -1},

                    {2, -2},
                    {3, -1},
                    {2, -1},
                    {3, -1},

                    {2, -2},
                    {3, -1},
                    {2, -2},
                    {3, -1},

                    {3, -1},
                    {2, -2},
                    {3, -1},
                    {2, -2},

                    {2, -3},
                    {3, -1},
                    {2, -1}
            };
    public static int[] nextShape=new int[]{
            1,2,3,0,5,6,7,4,9,8,11,10,13,14,15,12,17,16,18
    };
}

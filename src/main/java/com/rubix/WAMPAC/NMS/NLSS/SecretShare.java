package com.rubix.WAMPAC.NMS.NLSS;

import java.util.Random;

public class SecretShare
{
    public static int[][] LA = {{0, 0, 0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 0, 0, 1, 0}, {0, 0, 0, 0, 0, 0,
            1, 1}, {0, 0, 0, 0, 0, 1, 0, 0}, {0, 0, 0, 0, 0, 1, 0, 1}, {0, 0, 0, 0,
            0, 1, 1, 0}, {0, 0, 0, 0, 0, 1, 1, 1}, {0, 0, 0, 0, 1, 0, 0, 0}, {0, 0,
            0, 0, 1, 0, 0, 1}, {0, 0, 0, 0, 1, 0, 1, 0}, {0, 0, 0, 0, 1, 0, 1, 1},
            {0, 0, 0, 0, 1, 1, 0, 0}, {0, 0, 0, 0, 1, 1, 0, 1}, {0, 0, 0, 0, 1, 1,
            1, 0}, {0, 0, 0, 1, 0, 0, 0, 0}, {0, 0, 0, 1, 0, 0, 0, 1}, {0, 0, 0, 1,
            0, 0, 1, 0}, {0, 0, 0, 1, 0, 0, 1, 1}, {0, 0, 0, 1, 0, 1, 0, 0}, {0, 0,
            0, 1, 0, 1, 0, 1}, {0, 0, 0, 1, 0, 1, 1, 0}, {0, 0, 0, 1, 0, 1, 1, 1},
            {0, 0, 0, 1, 1, 0, 0, 0}, {0, 0, 0, 1, 1, 0, 0, 1}, {0, 0, 0, 1, 1, 0,
            1, 0}, {0, 0, 0, 1, 1, 0, 1, 1}, {0, 0, 0, 1, 1, 1, 0, 0}, {0, 0, 0, 1,
            1, 1, 0, 1}, {0, 0, 0, 1, 1, 1, 1, 0}, {0, 0, 0, 1, 1, 1, 1, 1}, {0, 0,
            1, 0, 0, 0, 0, 0}, {0, 0, 1, 0, 0, 0, 0, 1}, {0, 0, 1, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 0, 1, 1}, {0, 0, 1, 0, 0, 1, 0, 0}, {0, 0, 1, 0, 0, 1,
            0, 1}, {0, 0, 1, 0, 0, 1, 1, 0}, {0, 0, 1, 0, 0, 1, 1, 1}, {0, 0, 1, 0,
            1, 0, 0, 0}, {0, 0, 1, 0, 1, 0, 0, 1}, {0, 0, 1, 0, 1, 0, 1, 0}, {0, 0,
            1, 0, 1, 0, 1, 1}, {0, 0, 1, 0, 1, 1, 0, 0}, {0, 0, 1, 0, 1, 1, 0, 1},
            {0, 0, 1, 0, 1, 1, 1, 0}, {0, 0, 1, 0, 1, 1, 1, 1}, {0, 0, 1, 1, 0, 0,
            0, 0}, {0, 0, 1, 1, 0, 0, 0, 1}, {0, 0, 1, 1, 0, 0, 1, 0}, {0, 0, 1, 1,
            0, 1, 0, 0}, {0, 0, 1, 1, 0, 1, 0, 1}, {0, 0, 1, 1, 0, 1, 1, 0}, {0, 0,
            1, 1, 0, 1, 1, 1}, {0, 0, 1, 1, 1, 0, 0, 0}, {0, 0, 1, 1, 1, 0, 0, 1},
            {0, 0, 1, 1, 1, 0, 1, 0}, {0, 0, 1, 1, 1, 0, 1, 1}, {0, 0, 1, 1, 1, 1,
            0, 1}, {0, 0, 1, 1, 1, 1, 1, 0}, {0, 0, 1, 1, 1, 1, 1, 1}, {0, 1, 0, 0,
            0, 0, 0, 0}, {0, 1, 0, 0, 0, 0, 0, 1}, {0, 1, 0, 0, 0, 0, 1, 0}, {0, 1,
            0, 0, 0, 0, 1, 1}, {0, 1, 0, 0, 0, 1, 0, 0}, {0, 1, 0, 0, 0, 1, 0, 1},
            {0, 1, 0, 0, 0, 1, 1, 0}, {0, 1, 0, 0, 0, 1, 1, 1}, {0, 1, 0, 0, 1, 0,
            0, 0}, {0, 1, 0, 0, 1, 0, 0, 1}, {0, 1, 0, 0, 1, 0, 1, 0}, {0, 1, 0, 0,
            1, 0, 1, 1}, {0, 1, 0, 0, 1, 1, 0, 0}, {0, 1, 0, 0, 1, 1, 0, 1}, {0, 1,
            0, 0, 1, 1, 1, 0}, {0, 1, 0, 0, 1, 1, 1, 1}, {0, 1, 0, 1, 0, 0, 0, 0},
            {0, 1, 0, 1, 0, 0, 0, 1}, {0, 1, 0, 1, 0, 0, 1, 0}, {0, 1, 0, 1, 0, 0,
            1, 1}, {0, 1, 0, 1, 0, 1, 0, 0}, {0, 1, 0, 1, 0, 1, 1, 0}, {0, 1, 0, 1,
            0, 1, 1, 1}, {0, 1, 0, 1, 1, 0, 0, 0}, {0, 1, 0, 1, 1, 0, 0, 1}, {0, 1,
            0, 1, 1, 0, 1, 1}, {0, 1, 0, 1, 1, 1, 0, 0}, {0, 1, 0, 1, 1, 1, 0, 1},
            {0, 1, 0, 1, 1, 1, 1, 0}, {0, 1, 0, 1, 1, 1, 1, 1}, {0, 1, 1, 0, 0, 0,
            0, 0}, {0, 1, 1, 0, 0, 0, 0, 1}, {0, 1, 1, 0, 0, 0, 1, 0}, {0, 1, 1, 0,
            0, 0, 1, 1}, {0, 1, 1, 0, 0, 1, 0, 0}, {0, 1, 1, 0, 0, 1, 0, 1}, {0, 1,
            1, 0, 0, 1, 1, 1}, {0, 1, 1, 0, 1, 0, 0, 0}, {0, 1, 1, 0, 1, 0, 1, 0},
            {0, 1, 1, 0, 1, 0, 1, 1}, {0, 1, 1, 0, 1, 1, 0, 0}, {0, 1, 1, 0, 1, 1,
            0, 1}, {0, 1, 1, 0, 1, 1, 1, 0}, {0, 1, 1, 0, 1, 1, 1, 1}, {0, 1, 1, 1,
            0, 0, 0, 0}, {0, 1, 1, 1, 0, 0, 0, 1}, {0, 1, 1, 1, 0, 0, 1, 0}, {0, 1,
            1, 1, 0, 0, 1, 1}, {0, 1, 1, 1, 0, 1, 0, 0}, {0, 1, 1, 1, 0, 1, 0, 1},
            {0, 1, 1, 1, 0, 1, 1, 0}, {0, 1, 1, 1, 0, 1, 1, 1}, {0, 1, 1, 1, 1, 0,
            0, 0}, {0, 1, 1, 1, 1, 0, 0, 1}, {0, 1, 1, 1, 1, 0, 1, 0}, {0, 1, 1, 1,
            1, 0, 1, 1}, {0, 1, 1, 1, 1, 1, 0, 0}, {0, 1, 1, 1, 1, 1, 0, 1}, {0, 1,
            1, 1, 1, 1, 1, 0}, {0, 1, 1, 1, 1, 1, 1, 1}, {1, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 1}, {1, 0, 0, 0, 0, 0, 1, 0}, {1, 0, 0, 0, 0, 0,
            1, 1}, {1, 0, 0, 0, 0, 1, 0, 0}, {1, 0, 0, 0, 0, 1, 0, 1}, {1, 0, 0, 0,
            0, 1, 1, 0}, {1, 0, 0, 0, 0, 1, 1, 1}, {1, 0, 0, 0, 1, 0, 0, 0}, {1, 0,
            0, 0, 1, 0, 0, 1}, {1, 0, 0, 0, 1, 0, 1, 0}, {1, 0, 0, 0, 1, 0, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 0}, {1, 0, 0, 0, 1, 1, 0, 1}, {1, 0, 0, 0, 1, 1,
            1, 0}, {1, 0, 0, 0, 1, 1, 1, 1}, {1, 0, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 1,
            0, 0, 0, 1}, {1, 0, 0, 1, 0, 0, 1, 0}, {1, 0, 0, 1, 0, 0, 1, 1}, {1, 0,
            0, 1, 0, 1, 0, 0}, {1, 0, 0, 1, 0, 1, 0, 1}, {1, 0, 0, 1, 0, 1, 1, 1},
            {1, 0, 0, 1, 1, 0, 0, 0}, {1, 0, 0, 1, 1, 0, 1, 0}, {1, 0, 0, 1, 1, 0,
            1, 1}, {1, 0, 0, 1, 1, 1, 0, 0}, {1, 0, 0, 1, 1, 1, 0, 1}, {1, 0, 0, 1,
            1, 1, 1, 0}, {1, 0, 0, 1, 1, 1, 1, 1}, {1, 0, 1, 0, 0, 0, 0, 0}, {1, 0,
            1, 0, 0, 0, 0, 1}, {1, 0, 1, 0, 0, 0, 1, 0}, {1, 0, 1, 0, 0, 0, 1, 1},
            {1, 0, 1, 0, 0, 1, 0, 0}, {1, 0, 1, 0, 0, 1, 1, 0}, {1, 0, 1, 0, 0, 1,
            1, 1}, {1, 0, 1, 0, 1, 0, 0, 0}, {1, 0, 1, 0, 1, 0, 0, 1}, {1, 0, 1, 0,
            1, 0, 1, 1}, {1, 0, 1, 0, 1, 1, 0, 0}, {1, 0, 1, 0, 1, 1, 0, 1}, {1, 0,
            1, 0, 1, 1, 1, 0}, {1, 0, 1, 0, 1, 1, 1, 1}, {1, 0, 1, 1, 0, 0, 0, 0},
            {1, 0, 1, 1, 0, 0, 0, 1}, {1, 0, 1, 1, 0, 0, 1, 0}, {1, 0, 1, 1, 0, 0,
            1, 1}, {1, 0, 1, 1, 0, 1, 0, 0}, {1, 0, 1, 1, 0, 1, 0, 1}, {1, 0, 1, 1,
            0, 1, 1, 0}, {1, 0, 1, 1, 0, 1, 1, 1}, {1, 0, 1, 1, 1, 0, 0, 0}, {1, 0,
            1, 1, 1, 0, 0, 1}, {1, 0, 1, 1, 1, 0, 1, 0}, {1, 0, 1, 1, 1, 0, 1, 1},
            {1, 0, 1, 1, 1, 1, 0, 0}, {1, 0, 1, 1, 1, 1, 0, 1}, {1, 0, 1, 1, 1, 1,
            1, 0}, {1, 0, 1, 1, 1, 1, 1, 1}, {1, 1, 0, 0, 0, 0, 0, 0}, {1, 1, 0, 0,
            0, 0, 0, 1}, {1, 1, 0, 0, 0, 0, 1, 0}, {1, 1, 0, 0, 0, 1, 0, 0}, {1, 1,
            0, 0, 0, 1, 0, 1}, {1, 1, 0, 0, 0, 1, 1, 0}, {1, 1, 0, 0, 0, 1, 1, 1},
            {1, 1, 0, 0, 1, 0, 0, 0}, {1, 1, 0, 0, 1, 0, 0, 1}, {1, 1, 0, 0, 1, 0,
            1, 0}, {1, 1, 0, 0, 1, 0, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 1}, {1, 1, 0, 0,
            1, 1, 1, 0}, {1, 1, 0, 0, 1, 1, 1, 1}, {1, 1, 0, 1, 0, 0, 0, 0}, {1, 1,
            0, 1, 0, 0, 0, 1}, {1, 1, 0, 1, 0, 0, 1, 0}, {1, 1, 0, 1, 0, 0, 1, 1},
            {1, 1, 0, 1, 0, 1, 0, 0}, {1, 1, 0, 1, 0, 1, 0, 1}, {1, 1, 0, 1, 0, 1,
            1, 0}, {1, 1, 0, 1, 0, 1, 1, 1}, {1, 1, 0, 1, 1, 0, 0, 0}, {1, 1, 0, 1,
            1, 0, 0, 1}, {1, 1, 0, 1, 1, 0, 1, 0}, {1, 1, 0, 1, 1, 0, 1, 1}, {1, 1,
            0, 1, 1, 1, 0, 0}, {1, 1, 0, 1, 1, 1, 0, 1}, {1, 1, 0, 1, 1, 1, 1, 0},
            {1, 1, 0, 1, 1, 1, 1, 1}, {1, 1, 1, 0, 0, 0, 0, 0}, {1, 1, 1, 0, 0, 0,
            0, 1}, {1, 1, 1, 0, 0, 0, 1, 0}, {1, 1, 1, 0, 0, 0, 1, 1}, {1, 1, 1, 0,
            0, 1, 0, 0}, {1, 1, 1, 0, 0, 1, 0, 1}, {1, 1, 1, 0, 0, 1, 1, 0}, {1, 1,
            1, 0, 0, 1, 1, 1}, {1, 1, 1, 0, 1, 0, 0, 0}, {1, 1, 1, 0, 1, 0, 0, 1},
            {1, 1, 1, 0, 1, 0, 1, 0}, {1, 1, 1, 0, 1, 0, 1, 1}, {1, 1, 1, 0, 1, 1,
            0, 0}, {1, 1, 1, 0, 1, 1, 0, 1}, {1, 1, 1, 0, 1, 1, 1, 0}, {1, 1, 1, 0,
            1, 1, 1, 1}, {1, 1, 1, 1, 0, 0, 0, 1}, {1, 1, 1, 1, 0, 0, 1, 0}, {1, 1,
            1, 1, 0, 0, 1, 1}, {1, 1, 1, 1, 0, 1, 0, 0}, {1, 1, 1, 1, 0, 1, 0, 1},
            {1, 1, 1, 1, 0, 1, 1, 0}, {1, 1, 1, 1, 0, 1, 1, 1}, {1, 1, 1, 1, 1, 0,
            0, 0}, {1, 1, 1, 1, 1, 0, 0, 1}, {1, 1, 1, 1, 1, 0, 1, 0}, {1, 1, 1, 1,
            1, 0, 1, 1}, {1, 1, 1, 1, 1, 1, 0, 0}, {1, 1, 1, 1, 1, 1, 0, 1}, {1, 1,
            1, 1, 1, 1, 1, 0}};

    public static int[][] V4 = {{0, 0, 0, 0}, {0, 0, 0, 1}, {0, 0, 1, 0}, {0, 0, 1, 1}, {0, 1, 0, 0},
            {0, 1, 0, 1}, {0, 1, 1, 0}, {0, 1, 1, 1}, {1, 0, 0, 0}, {1, 0, 0, 1},
            {1, 0, 1, 0}, {1, 0, 1, 1}, {1, 1, 0, 0}, {1, 1, 0, 1}, {1, 1, 1, 0},
            {1, 1, 1, 1}};

    public static int[][] V8 = {{0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 1}, {0, 0, 0, 0, 0, 0,
            1, 0}, {0, 0, 0, 0, 0, 0, 1, 1}, {0, 0, 0, 0, 0, 1, 0, 0}, {0, 0, 0, 0,
            0, 1, 0, 1}, {0, 0, 0, 0, 0, 1, 1, 0}, {0, 0, 0, 0, 0, 1, 1, 1}, {0, 0,
            0, 0, 1, 0, 0, 0}, {0, 0, 0, 0, 1, 0, 0, 1}, {0, 0, 0, 0, 1, 0, 1, 0},
            {0, 0, 0, 0, 1, 0, 1, 1}, {0, 0, 0, 0, 1, 1, 0, 0}, {0, 0, 0, 0, 1, 1,
            0, 1}, {0, 0, 0, 0, 1, 1, 1, 0}, {0, 0, 0, 0, 1, 1, 1, 1}, {0, 0, 0, 1,
            0, 0, 0, 0}, {0, 0, 0, 1, 0, 0, 0, 1}, {0, 0, 0, 1, 0, 0, 1, 0}, {0, 0,
            0, 1, 0, 0, 1, 1}, {0, 0, 0, 1, 0, 1, 0, 0}, {0, 0, 0, 1, 0, 1, 0, 1},
            {0, 0, 0, 1, 0, 1, 1, 0}, {0, 0, 0, 1, 0, 1, 1, 1}, {0, 0, 0, 1, 1, 0,
            0, 0}, {0, 0, 0, 1, 1, 0, 0, 1}, {0, 0, 0, 1, 1, 0, 1, 0}, {0, 0, 0, 1,
            1, 0, 1, 1}, {0, 0, 0, 1, 1, 1, 0, 0}, {0, 0, 0, 1, 1, 1, 0, 1}, {0, 0,
            0, 1, 1, 1, 1, 0}, {0, 0, 0, 1, 1, 1, 1, 1}, {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 1}, {0, 0, 1, 0, 0, 0, 1, 0}, {0, 0, 1, 0, 0, 0,
            1, 1}, {0, 0, 1, 0, 0, 1, 0, 0}, {0, 0, 1, 0, 0, 1, 0, 1}, {0, 0, 1, 0,
            0, 1, 1, 0}, {0, 0, 1, 0, 0, 1, 1, 1}, {0, 0, 1, 0, 1, 0, 0, 0}, {0, 0,
            1, 0, 1, 0, 0, 1}, {0, 0, 1, 0, 1, 0, 1, 0}, {0, 0, 1, 0, 1, 0, 1, 1},
            {0, 0, 1, 0, 1, 1, 0, 0}, {0, 0, 1, 0, 1, 1, 0, 1}, {0, 0, 1, 0, 1, 1,
            1, 0}, {0, 0, 1, 0, 1, 1, 1, 1}, {0, 0, 1, 1, 0, 0, 0, 0}, {0, 0, 1, 1,
            0, 0, 0, 1}, {0, 0, 1, 1, 0, 0, 1, 0}, {0, 0, 1, 1, 0, 0, 1, 1}, {0, 0,
            1, 1, 0, 1, 0, 0}, {0, 0, 1, 1, 0, 1, 0, 1}, {0, 0, 1, 1, 0, 1, 1, 0},
            {0, 0, 1, 1, 0, 1, 1, 1}, {0, 0, 1, 1, 1, 0, 0, 0}, {0, 0, 1, 1, 1, 0,
            0, 1}, {0, 0, 1, 1, 1, 0, 1, 0}, {0, 0, 1, 1, 1, 0, 1, 1}, {0, 0, 1, 1,
            1, 1, 0, 0}, {0, 0, 1, 1, 1, 1, 0, 1}, {0, 0, 1, 1, 1, 1, 1, 0}, {0, 0,
            1, 1, 1, 1, 1, 1}, {0, 1, 0, 0, 0, 0, 0, 0}, {0, 1, 0, 0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0, 0, 1, 0}, {0, 1, 0, 0, 0, 0, 1, 1}, {0, 1, 0, 0, 0, 1,
            0, 0}, {0, 1, 0, 0, 0, 1, 0, 1}, {0, 1, 0, 0, 0, 1, 1, 0}, {0, 1, 0, 0,
            0, 1, 1, 1}, {0, 1, 0, 0, 1, 0, 0, 0}, {0, 1, 0, 0, 1, 0, 0, 1}, {0, 1,
            0, 0, 1, 0, 1, 0}, {0, 1, 0, 0, 1, 0, 1, 1}, {0, 1, 0, 0, 1, 1, 0, 0},
            {0, 1, 0, 0, 1, 1, 0, 1}, {0, 1, 0, 0, 1, 1, 1, 0}, {0, 1, 0, 0, 1, 1,
            1, 1}, {0, 1, 0, 1, 0, 0, 0, 0}, {0, 1, 0, 1, 0, 0, 0, 1}, {0, 1, 0, 1,
            0, 0, 1, 0}, {0, 1, 0, 1, 0, 0, 1, 1}, {0, 1, 0, 1, 0, 1, 0, 0}, {0, 1,
            0, 1, 0, 1, 0, 1}, {0, 1, 0, 1, 0, 1, 1, 0}, {0, 1, 0, 1, 0, 1, 1, 1},
            {0, 1, 0, 1, 1, 0, 0, 0}, {0, 1, 0, 1, 1, 0, 0, 1}, {0, 1, 0, 1, 1, 0,
            1, 0}, {0, 1, 0, 1, 1, 0, 1, 1}, {0, 1, 0, 1, 1, 1, 0, 0}, {0, 1, 0, 1,
            1, 1, 0, 1}, {0, 1, 0, 1, 1, 1, 1, 0}, {0, 1, 0, 1, 1, 1, 1, 1}, {0, 1,
            1, 0, 0, 0, 0, 0}, {0, 1, 1, 0, 0, 0, 0, 1}, {0, 1, 1, 0, 0, 0, 1, 0},
            {0, 1, 1, 0, 0, 0, 1, 1}, {0, 1, 1, 0, 0, 1, 0, 0}, {0, 1, 1, 0, 0, 1,
            0, 1}, {0, 1, 1, 0, 0, 1, 1, 0}, {0, 1, 1, 0, 0, 1, 1, 1}, {0, 1, 1, 0,
            1, 0, 0, 0}, {0, 1, 1, 0, 1, 0, 0, 1}, {0, 1, 1, 0, 1, 0, 1, 0}, {0, 1,
            1, 0, 1, 0, 1, 1}, {0, 1, 1, 0, 1, 1, 0, 0}, {0, 1, 1, 0, 1, 1, 0, 1},
            {0, 1, 1, 0, 1, 1, 1, 0}, {0, 1, 1, 0, 1, 1, 1, 1}, {0, 1, 1, 1, 0, 0,
            0, 0}, {0, 1, 1, 1, 0, 0, 0, 1}, {0, 1, 1, 1, 0, 0, 1, 0}, {0, 1, 1, 1,
            0, 0, 1, 1}, {0, 1, 1, 1, 0, 1, 0, 0}, {0, 1, 1, 1, 0, 1, 0, 1}, {0, 1,
            1, 1, 0, 1, 1, 0}, {0, 1, 1, 1, 0, 1, 1, 1}, {0, 1, 1, 1, 1, 0, 0, 0},
            {0, 1, 1, 1, 1, 0, 0, 1}, {0, 1, 1, 1, 1, 0, 1, 0}, {0, 1, 1, 1, 1, 0,
            1, 1}, {0, 1, 1, 1, 1, 1, 0, 0}, {0, 1, 1, 1, 1, 1, 0, 1}, {0, 1, 1, 1,
            1, 1, 1, 0}, {0, 1, 1, 1, 1, 1, 1, 1}, {1, 0, 0, 0, 0, 0, 0, 0}, {1, 0,
            0, 0, 0, 0, 0, 1}, {1, 0, 0, 0, 0, 0, 1, 0}, {1, 0, 0, 0, 0, 0, 1, 1},
            {1, 0, 0, 0, 0, 1, 0, 0}, {1, 0, 0, 0, 0, 1, 0, 1}, {1, 0, 0, 0, 0, 1,
            1, 0}, {1, 0, 0, 0, 0, 1, 1, 1}, {1, 0, 0, 0, 1, 0, 0, 0}, {1, 0, 0, 0,
            1, 0, 0, 1}, {1, 0, 0, 0, 1, 0, 1, 0}, {1, 0, 0, 0, 1, 0, 1, 1}, {1, 0,
            0, 0, 1, 1, 0, 0}, {1, 0, 0, 0, 1, 1, 0, 1}, {1, 0, 0, 0, 1, 1, 1, 0},
            {1, 0, 0, 0, 1, 1, 1, 1}, {1, 0, 0, 1, 0, 0, 0, 0}, {1, 0, 0, 1, 0, 0,
            0, 1}, {1, 0, 0, 1, 0, 0, 1, 0}, {1, 0, 0, 1, 0, 0, 1, 1}, {1, 0, 0, 1,
            0, 1, 0, 0}, {1, 0, 0, 1, 0, 1, 0, 1}, {1, 0, 0, 1, 0, 1, 1, 0}, {1, 0,
            0, 1, 0, 1, 1, 1}, {1, 0, 0, 1, 1, 0, 0, 0}, {1, 0, 0, 1, 1, 0, 0, 1},
            {1, 0, 0, 1, 1, 0, 1, 0}, {1, 0, 0, 1, 1, 0, 1, 1}, {1, 0, 0, 1, 1, 1,
            0, 0}, {1, 0, 0, 1, 1, 1, 0, 1}, {1, 0, 0, 1, 1, 1, 1, 0}, {1, 0, 0, 1,
            1, 1, 1, 1}, {1, 0, 1, 0, 0, 0, 0, 0}, {1, 0, 1, 0, 0, 0, 0, 1}, {1, 0,
            1, 0, 0, 0, 1, 0}, {1, 0, 1, 0, 0, 0, 1, 1}, {1, 0, 1, 0, 0, 1, 0, 0},
            {1, 0, 1, 0, 0, 1, 0, 1}, {1, 0, 1, 0, 0, 1, 1, 0}, {1, 0, 1, 0, 0, 1,
            1, 1}, {1, 0, 1, 0, 1, 0, 0, 0}, {1, 0, 1, 0, 1, 0, 0, 1}, {1, 0, 1, 0,
            1, 0, 1, 0}, {1, 0, 1, 0, 1, 0, 1, 1}, {1, 0, 1, 0, 1, 1, 0, 0}, {1, 0,
            1, 0, 1, 1, 0, 1}, {1, 0, 1, 0, 1, 1, 1, 0}, {1, 0, 1, 0, 1, 1, 1, 1},
            {1, 0, 1, 1, 0, 0, 0, 0}, {1, 0, 1, 1, 0, 0, 0, 1}, {1, 0, 1, 1, 0, 0,
            1, 0}, {1, 0, 1, 1, 0, 0, 1, 1}, {1, 0, 1, 1, 0, 1, 0, 0}, {1, 0, 1, 1,
            0, 1, 0, 1}, {1, 0, 1, 1, 0, 1, 1, 0}, {1, 0, 1, 1, 0, 1, 1, 1}, {1, 0,
            1, 1, 1, 0, 0, 0}, {1, 0, 1, 1, 1, 0, 0, 1}, {1, 0, 1, 1, 1, 0, 1, 0},
            {1, 0, 1, 1, 1, 0, 1, 1}, {1, 0, 1, 1, 1, 1, 0, 0}, {1, 0, 1, 1, 1, 1,
            0, 1}, {1, 0, 1, 1, 1, 1, 1, 0}, {1, 0, 1, 1, 1, 1, 1, 1}, {1, 1, 0, 0,
            0, 0, 0, 0}, {1, 1, 0, 0, 0, 0, 0, 1}, {1, 1, 0, 0, 0, 0, 1, 0}, {1, 1,
            0, 0, 0, 0, 1, 1}, {1, 1, 0, 0, 0, 1, 0, 0}, {1, 1, 0, 0, 0, 1, 0, 1},
            {1, 1, 0, 0, 0, 1, 1, 0}, {1, 1, 0, 0, 0, 1, 1, 1}, {1, 1, 0, 0, 1, 0,
            0, 0}, {1, 1, 0, 0, 1, 0, 0, 1}, {1, 1, 0, 0, 1, 0, 1, 0}, {1, 1, 0, 0,
            1, 0, 1, 1}, {1, 1, 0, 0, 1, 1, 0, 0}, {1, 1, 0, 0, 1, 1, 0, 1}, {1, 1,
            0, 0, 1, 1, 1, 0}, {1, 1, 0, 0, 1, 1, 1, 1}, {1, 1, 0, 1, 0, 0, 0, 0},
            {1, 1, 0, 1, 0, 0, 0, 1}, {1, 1, 0, 1, 0, 0, 1, 0}, {1, 1, 0, 1, 0, 0,
            1, 1}, {1, 1, 0, 1, 0, 1, 0, 0}, {1, 1, 0, 1, 0, 1, 0, 1}, {1, 1, 0, 1,
            0, 1, 1, 0}, {1, 1, 0, 1, 0, 1, 1, 1}, {1, 1, 0, 1, 1, 0, 0, 0}, {1, 1,
            0, 1, 1, 0, 0, 1}, {1, 1, 0, 1, 1, 0, 1, 0}, {1, 1, 0, 1, 1, 0, 1, 1},
            {1, 1, 0, 1, 1, 1, 0, 0}, {1, 1, 0, 1, 1, 1, 0, 1}, {1, 1, 0, 1, 1, 1,
            1, 0}, {1, 1, 0, 1, 1, 1, 1, 1}, {1, 1, 1, 0, 0, 0, 0, 0}, {1, 1, 1, 0,
            0, 0, 0, 1}, {1, 1, 1, 0, 0, 0, 1, 0}, {1, 1, 1, 0, 0, 0, 1, 1}, {1, 1,
            1, 0, 0, 1, 0, 0}, {1, 1, 1, 0, 0, 1, 0, 1}, {1, 1, 1, 0, 0, 1, 1, 0},
            {1, 1, 1, 0, 0, 1, 1, 1}, {1, 1, 1, 0, 1, 0, 0, 0}, {1, 1, 1, 0, 1, 0,
            0, 1}, {1, 1, 1, 0, 1, 0, 1, 0}, {1, 1, 1, 0, 1, 0, 1, 1}, {1, 1, 1, 0,
            1, 1, 0, 0}, {1, 1, 1, 0, 1, 1, 0, 1}, {1, 1, 1, 0, 1, 1, 1, 0}, {1, 1,
            1, 0, 1, 1, 1, 1}, {1, 1, 1, 1, 0, 0, 0, 0}, {1, 1, 1, 1, 0, 0, 0, 1},
            {1, 1, 1, 1, 0, 0, 1, 0}, {1, 1, 1, 1, 0, 0, 1, 1}, {1, 1, 1, 1, 0, 1,
            0, 0}, {1, 1, 1, 1, 0, 1, 0, 1}, {1, 1, 1, 1, 0, 1, 1, 0}, {1, 1, 1, 1,
            0, 1, 1, 1}, {1, 1, 1, 1, 1, 0, 0, 0}, {1, 1, 1, 1, 1, 0, 0, 1}, {1, 1,
            1, 1, 1, 0, 1, 0}, {1, 1, 1, 1, 1, 0, 1, 1}, {1, 1, 1, 1, 1, 1, 0, 0},
            {1, 1, 1, 1, 1, 1, 0, 1}, {1, 1, 1, 1, 1, 1, 1, 0}, {1, 1, 1, 1, 1, 1,
            1, 1}};

    public static int[][] G = {{1, 1, 1, 1, 1, 1, 1, 1},
            {0, 1, 0, 1, 0, 1, 0, 1},
            {0, 0, 1, 1, 0, 0, 1, 1},
            {0, 0, 0, 0, 1, 1, 1, 1}};

    public static int i,s;

    //static Set<Integer> Xset = new HashSet<Integer>();


    static int[] X1 =new int[4];
    static int[] alpha1 =new int[8];
    static int[] Y1 =new int[8];
    static int[] Y2 =new int[8];
    static int[] Y3 =new int[8];
    static int[] Y4 =new int[8];
//    static int[] Y5 =new int[8];
//    static int[] Y6 =new int[8];

    static int[] S0;

    SecretShare(int n)
    {
        s=n;
    }

    public static int generaterandom(int length)
    {
        int r = new Random().nextInt(length);
        return r;
    }



    public static int[] genarray(int arr[][],int size)// To get random elements from any arrays
    {
        int j;
        int[] temp = new int[size];
        int r = generaterandom(arr.length);
        for (j = 0; j < size; j++) {
            temp[j] = arr[r][j];
        }

        return temp;
    }



    public static int[] multiplyMatrices(int[] firstMatrix, int[][] secondMatrix,int c1, int c2)
    {
        int[] product = new int[c2];
        int i,j,sums=0;
        for(i=0;i<c2;i++)
        {
            for(j=0;j<c1;j++)
            {
                sums+=secondMatrix[j][i]*firstMatrix[j];
            }
            product[i]=sums;
            sums=0;
        }

        return product;
    }


    public static int[] checkcomply(int[] cand, int[] S0)
    {
        int sum=5;  //can be any number greater than 's', so that we can enter the while loop
        while(sum>s)
        {
            sum = 0;
            for (i = 0; i < cand.length; i++)
                sum = sum + (cand[i] * S0[i]);
            sum=sum%2;
            if (sum != s)           //if not valid
            {
                cand = genarray(V8, 8); //get new Y
                cand = checkcomply(cand, S0); //check new Y
            }
        }
        return cand;

    }


    public void starts()
    {

        X1 = genarray(V4,4);
        alpha1 = genarray(LA,8);

        Y1 = genarray(V8,8);
        Y2 = genarray(V8,8);
        Y3 = genarray(V8,8);
        Y4 = genarray(V8,8);
//        Y5 = genarray(V8,8);
//        Y6 = genarray(V8,8);
        int[] tempmat;
        tempmat = multiplyMatrices(X1,G,4,8);

        S0 = new int[tempmat.length];

        for(i=0;i<alpha1.length;i++)
            S0[i]=(tempmat[i]+alpha1[i])%2;

        Y1 = checkcomply(Y1,S0);
        Y2 = checkcomply(Y2,S0);
        Y3 = checkcomply(Y2,S0);
        Y4 = checkcomply(Y2,S0);
//        Y5 = checkcomply(Y2,S0);
//        Y6 = checkcomply(Y2,S0);


    }
}





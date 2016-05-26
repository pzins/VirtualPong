package com.mi12.pierre.virtualpong;

import java.io.Serializable;

/**
 * Created by pierre on 25/05/16.
 */
//Objet contenant les positions du jeu
//il est envoyé par les sockets
public class GamePositions implements Serializable
{
    float player_x;
    float opp_x;
    float ball_x;
    float ball_y;
    int scorePlayer;
    int scoreOpp;

    GamePositions(float _player_x, float _opp_x, float _ball_x, float _ball_y, int _scorePlayer, int _scoreOpp){
        player_x = _player_x;
        opp_x = _opp_x;
        ball_x = _ball_x;
        ball_y = _ball_y;
        scorePlayer = _scorePlayer;
        scoreOpp = _scoreOpp;
    }
}
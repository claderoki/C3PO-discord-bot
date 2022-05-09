package com.c3po.command.hangman.game;

public class HangmanStateHelper {
    private final static String[] states = {"""
  ╤═══╗
      ║
      ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
      ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
  │   ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
  │\\  ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
 /│\\  ║
      ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
 /│\\  ║
   \\  ║
      ║
══════╩═""", """
  ╤═══╗
  o   ║
 /│\\  ║
 / \\  ║
      ║
══════╩═
"""};

    public static String getState(int state) {
        return states[Math.min(state, states.length)];
    }

    public static int getMaxStates() {
        return states.length;
    }

}

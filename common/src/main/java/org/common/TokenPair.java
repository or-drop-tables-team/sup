package org.common;

/**
 * This is used to represent the tokenized protocol messages, but really just
 * one word at a time. The first split is kept in 'first', the rest in 'rest'.
 *
 */
public class TokenPair
{
    public final String first;
    public final String rest;

    public TokenPair(String x, String y) {
    	this.first = x;
    	this.rest = y;
    }
}

package com.project;

public class Hash {
    private long state;

    public Hash(long seed) {
        // Initialize state; adding a constant ensures even a 0 seed works
        this.state = seed + 0x9E3779B9; 
        next(); // Advance once to "warm up" the state
    }

    public long next() {
        // LCG step updates the internal state
        state = state * 747796129405l + 289133236441235233l;
        
        // Permutation step produces the output
        long word = ((state >>> ((state >>> 28) + 4)) ^ state) * 277124151803737l;
        return (word >>> 22) ^ word;
    }
}

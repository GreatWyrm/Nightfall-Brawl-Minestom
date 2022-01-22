package me.arcanewarrior.com.damage;

import java.text.DecimalFormat;

/**
 * Borrowed from Nightfall code
 */
public class MultiPartDamage {
    private final double base;
    private double boost;
    private double multiplier;
    private double postBoost;


    public void addBoost(double amt) {
        boost += amt;
    }
    public void timesMult(double amt) {
        multiplier *= amt;
    }
    public void addPostBoost(double amt) {
        postBoost += amt;
    }

    public MultiPartDamage(double base) {
        this(base, 0, 1);
    }

    public MultiPartDamage(double base, double boost, double multiplier) {
        this(base, boost, multiplier, 0);
    }

    protected MultiPartDamage(double base, double boost, double multiplier, double postBoost) {
        this.base = base;
        this.boost = boost;
        this.multiplier = multiplier;
        this.postBoost = 0;
    }

    public double getValue() {
        return (base + boost) * multiplier + postBoost;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.####");

        return "Base: " + df.format(base)
                + " Boost: " + df.format(boost)
                + " Mult: " + df.format(multiplier)
                + " Postboost: " + df.format(postBoost)
                + " (Total: " + df.format(getValue()) + ")";
    }

    public MultiPartDamage cloneDamage() {
        return new MultiPartDamage(base, boost, multiplier, postBoost);
    }
}

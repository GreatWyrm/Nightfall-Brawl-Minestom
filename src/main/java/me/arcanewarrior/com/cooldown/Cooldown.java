package me.arcanewarrior.com.cooldown;

public interface Cooldown {


    /**
     * Checks if the cooldown is available to be used
     * @return true if the cooldown can be used, false otherwise
     */
    boolean hasCooledDown();
    /**
     * Attempts to use this cooldown, where a use is typically running a function
     * @return true if the cooldown was successfully used, false otherwise
     */
    boolean tryUse();

    /**
     * Resets the cooldown timer back to the maximum amount
     */
    void reset();

    /**
     * Updates the cooldown, generally called on a set interval (e.g. once per tick)
     */
    void update();

    /**
     * Reduces the cooldown by a certain amount
     * @param amount The amount to reduce the cooldown by
     */
    void reduceCooldown(int amount);

    /**
     * Gets the percentage that this cooldown has progressed, ranging from 0 (fully cooled down) and 1 (not cooled down)
     * @return The cooldown percentage, ranging from 0 to 1
     */
    float getCooldownPercentage();
}

package me.arcanewarrior.com.cooldown;

public abstract class AbstractCooldown implements Cooldown {

    // The maximum time that this cooldown can be at
    protected final int maximumTime;
    // The current time this cooldown can be at, bounded between 0 and maximumTime
    // When this number is at 0, the cooldown is considered available to be used
    protected int currentTime;

    public AbstractCooldown(int maximumTime) {
        if (maximumTime <= 0) throw new IllegalArgumentException("Cooldown max time must be strictly positive.");

        this.maximumTime = maximumTime;
        currentTime = 0;
    }

    @Override
    public boolean hasCooledDown() {
        return currentTime == 0;
    }

    @Override
    public boolean tryUse() {
        if(!hasCooledDown()) return false;

        reset();
        onUse();
        return true;
    }

    @Override
    public void reset() {
        currentTime = maximumTime;
    }

    @Override
    public void update() {
        reduceCooldown(1);
    }

    @Override
    public void reduceCooldown(int amount) {
        if (currentTime == 0) return;

        currentTime -= amount;
        if (currentTime < 0) {
            currentTime = 0;
        }
    }

    @Override
    public float getCooldownPercentage() {
        return (float) currentTime / maximumTime;
    }

    protected abstract void onUse();
}

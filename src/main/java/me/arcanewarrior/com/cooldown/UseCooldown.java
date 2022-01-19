package me.arcanewarrior.com.cooldown;

public class UseCooldown extends AbstractCooldown {

    private final Runnable useFunction;

    public UseCooldown(int maximumTime, Runnable useFunction) {
        super(maximumTime);
        this.useFunction = useFunction;
    }

    @Override
    protected void onUse() {
        useFunction.run();
    }
}

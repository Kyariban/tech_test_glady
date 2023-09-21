package com.glady.challenge.usecase.model.deposit.date;

import java.util.Date;

/**
 * The goal of this interface is to allow for more fluid testing without degrading the actual codebase
 */
public interface DateProvider {
    Date getCurrentDate();
}

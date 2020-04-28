package com.fantastic4.server.common.reservation;

import java.rmi.Remote;

public interface Reservation extends Remote {

    public boolean reserve(Object id)throws Exception;

    public boolean release(Object id)throws Exception;

}

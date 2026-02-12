package ar.ed.unlu;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

public class Util {

    public static ArrayList<String> getIpDisponibles() {
        ArrayList<String> listaIps = new ArrayList<>();
        // Siempre agregamos localhost por si quieres probar sola en una maquina
        listaIps.add("127.0.0.1");

        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                if (netint.isUp() && !netint.isLoopback() && !netint.isVirtual()) {
                    Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                    for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                        // Solo filtramos IPv4 (ej: 192.168.1.5)
                        if (inetAddress instanceof Inet4Address) {
                            String ip = inetAddress.getHostAddress();
                            if (!listaIps.contains(ip)) {
                                listaIps.add(ip);
                            }
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return listaIps;
    }
}
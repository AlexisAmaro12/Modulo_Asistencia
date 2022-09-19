package com.metro.modasistencia.security;

import com.metro.modasistencia.modelo.Rol;
import com.metro.modasistencia.repositorio.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.metro.modasistencia.modelo.Usuario appUser = usuarioRepositorio.findById(Integer.parseInt(username)).orElseThrow(() -> new UsernameNotFoundException("Usuario de inicio invalido"));

        Set grantList = new HashSet();
        for (Rol rol: appUser.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(rol.getDescripcion());
            grantList.add(grantedAuthority);
        }
        UserDetails user = (UserDetails) new User(username, appUser.getPassword(),grantList);
        return user;
    }
}

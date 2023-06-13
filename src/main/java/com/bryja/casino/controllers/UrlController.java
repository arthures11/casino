package com.bryja.casino.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UrlController {
    @RequestMapping(value="/adminpanelbonuses")
    public ModelAndView getadminbon() {
        return new ModelAndView("adminpanelbonuses");
    }    @RequestMapping(value="/bonuses")
    public ModelAndView getbonuses() {
        return new ModelAndView("bonuses");
    }
    @RequestMapping(value="/adminpanelusers")
    public ModelAndView getadminusers() {
        return new ModelAndView("adminpanelusers");
    }
    @RequestMapping(value="/login")
    public ModelAndView getlogi() {
        return new ModelAndView("login");
    }
    @RequestMapping(value="/profile")
    public ModelAndView getprof() {
        return new ModelAndView("profile");
    }
    @RequestMapping(value="/register")
    public ModelAndView getregister() {
        return new ModelAndView("register");
    }
    @RequestMapping(value="/coinflip")
    public ModelAndView gettable() {
        return new ModelAndView("coinflip");
    }
    @RequestMapping(value="/ruleta")
    public ModelAndView getRuleta() {
        return new ModelAndView("ruleta");
    }

    @RequestMapping(value="/")
    public ModelAndView getindex() {
        return new ModelAndView("index");
    }

}

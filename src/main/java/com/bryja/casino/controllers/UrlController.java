package com.bryja.casino.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UrlController {

   // @RequestMapping(value="/dashboard")
   // public ModelAndView getDashboard() {
     //   return new ModelAndView("dashboard");
   // }
   // @RequestMapping(value="/profile")
  //  public ModelAndView getProfile() {
     //   return new ModelAndView("profile");
  //  }
   // @RequestMapping(value="/projects")
   // public ModelAndView getProjects() {
    //    return new ModelAndView("projects");
   // }
 //   @RequestMapping(value="/projekt")
  //  public ModelAndView getProject() {
    //    return new ModelAndView("projekt");
  //  }

    @RequestMapping(value="/")
    public ModelAndView getindex() {
        return new ModelAndView("index");
    }

}

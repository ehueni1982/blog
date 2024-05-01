package com.blog.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialException;

import com.blog.model.Article;
import com.blog.service.ArticleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

;



@Controller
public class AppController {
	
	//importation article
	@Autowired
	private ArticleService articleService;
	
	@Value("${uploadDir}")
	private String uploadFolder;
	
	
	@GetMapping("/administration")
	public String administration() {
		return "index";
	
	}
	
	@GetMapping("gestionArticle")
	public String gestionArticle(Model model) {
		
		List<Article> listArticles = articleService.getAllArticles();
		
		model.addAttribute("listArticles", listArticles);
		return "administration/gestionArticle";
		
		
	}
	
	@GetMapping("/image/display/{id}")
	@ResponseBody
	public void showImage(@PathVariable("id") Long id, HttpServletResponse response, Optional<Article> article) throws SerialException, IOException {
		
		article = articleService.getArticleById(id);
		
		response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
		response.getOutputStream().write(article.get().getImage());
		response.getOutputStream().close();
	}
	
	@GetMapping("/creationArticle")
	public String creationArticle(Model model, Article article) {
		
		model.addAttribute("article", article);
		return "/administration/creationArticle";
		
		
		
	}
	//Récupération des objets
	@PostMapping("/enregistrerArticle")
	public RedirectView enregistrer(Model model, @RequestParam("titre") String titre,
												 @RequestParam("contenu") String contenu,
	 											 @RequestParam("redacteur") String redacteur,
	 											 HttpServletRequest request,
	 											 final @RequestParam("image") MultipartFile file) throws IOException{
													
		String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
		String fileName = file.getOriginalFilename();
		String filePath = Paths.get(uploadDirectory, fileName).toString();
		//Vérifions si le nom du fichier contient des caractères invalides
		if(fileName == null || fileName.contains("..")){
			model.addAttribute("invalid");
		}
		try {
			File dir = new File(uploadDirectory);
			
				if(!dir.exists()) {
					dir.mkdirs();
				}
				
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
				stream.write(file.getBytes());
				stream.close();
				
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		byte[] imageData = file.getBytes();
		
		Article articles = new Article();
		articles.setTitre(titre);
		articles.setContenu(contenu);
		articles.setRedacteur(redacteur);
		articles.setImage(imageData);
		
		articleService.saveArticle(articles);
		
		
		return new RedirectView("gestionArticle", true);
	}
	
	@GetMapping("/supprimerArticle/{id}")
	public ModelAndView supprimer(@PathVariable("id") Long id) {
		
		Optional<Article> article = articleService.getArticleById(id);
		
		if(article.isPresent()) {
			articleService.deleteArticle(id);
			
			return new ModelAndView("redirect:/gestionArticle");
			
		}else {
			return new ModelAndView("redirect:/gestionArticle");
		}
		
	}
	
	@GetMapping("modificationArticle/{id}")
	public String modificationArticle(@PathVariable(value = "id") Long id, Model model) {
		
		Optional<Article> article = articleService.getArticleById(id);
		
		model.addAttribute("article", article);
		
		return "administration/modificationArticle";
	}
	
	@PostMapping("/modification/{id}")
	public RedirectView modification(Model model, @RequestParam("titre") String titre,
												 @PathVariable("id") Long id,
												 @RequestParam("contenu") String contenu,
	 											 @RequestParam("redacteur") String redacteur,
	 											 HttpServletRequest request,
	 											 final @RequestParam("image") MultipartFile file) throws IOException{
													
		String uploadDirectory = request.getServletContext().getRealPath(uploadFolder);
		String fileName = file.getOriginalFilename();
		String filePath = Paths.get(uploadDirectory, fileName).toString();
		//Vérifions si le nom du fichier contient des caractères invalides
		if(fileName == null || fileName.contains("..")){
			model.addAttribute("invalid");
		}
		try {
			File dir = new File(uploadDirectory);
			
				if(!dir.exists()) {
					dir.mkdirs();
				}
				
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
				stream.write(file.getBytes());
				stream.close();
				
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
		byte[] imageData = file.getBytes();
		
		Article articles = articleService.getArticleById(id).orElse(null);	
		
		articles.setTitre(titre);
		articles.setContenu(contenu);
		articles.setRedacteur(redacteur);
		articles.setImage(imageData);
		
		articleService.updateArticle(articles);
		
		
		return new RedirectView("/gestionArticle", true);
	}
	
}

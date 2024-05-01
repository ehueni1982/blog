package com.blog.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.blog.model.Article;
import com.blog.repository.ArticleRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ArticleService {
	
	//import ArticleRepository
	@Autowired
	private ArticleRepository repo;
	
	
	//Récupération des articles
	
	public List<Article> getAllArticles(){
		
		return repo.findAll();
	}
	
	//Recupération un seul Article
	public Optional<Article> getArticleById(Long id){
		return repo.findById(id);
	}
	
	//Sauvegarder un article
	public void saveArticle(Article article) {
		repo.save(article);
	}
	
	//Suppression d'un article 
	public void deleteArticle(long id) {
		repo.deleteById(id);
	}
	
	//Modification d'un article
	public void updateArticle(Article updateArticle) {
		
		Optional<Article> existingArticleOptional = repo.findById(updateArticle.getId());
		
		if(existingArticleOptional.isPresent()){
			
			Article existingArticles = existingArticleOptional.get();
			existingArticles.setTitre(updateArticle.getTitre());
			existingArticles.setContenu(updateArticle.getContenu());
			existingArticles.setRedacteur(updateArticle.getRedacteur());
			existingArticles.setImage(updateArticle.getImage());
			
			repo.saveAndFlush(existingArticles);
			
		}
	}

}

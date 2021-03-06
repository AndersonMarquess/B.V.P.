package com.andersonmarques.bvp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.andersonmarques.bvp.model.Categoria;
import com.andersonmarques.bvp.model.Livro;
import com.andersonmarques.bvp.repository.CategoriaRepository;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Cacheable("categoriaBuscarTodasPorIdLivro")
	public List<Categoria> buscarTodasCategoriasPorIdLivro(String id) {
		System.out.println("Consulta no banco - categoria por id email " + id);
		return categoriaRepository.findAllByLivrosId(id);
	}

	void adicionarTodasAsCategoriaNoLivro(Livro livro, List<Categoria> categorias) {
		for (Categoria c : categorias) {
			Optional<Categoria> categoriaResult = categoriaRepository.findByNome(c.getNome());

			if (categoriaResult.isPresent()) {
				adicionarLivroNaCategoriaExistente(livro, c, categoriaResult.get());
			} else {
				c.adicionarLivro(livro);
				adicionarCategoria(c);
			}
		}
	}

	private void adicionarLivroNaCategoriaExistente(Livro livro, Categoria categoriaAntiga,
			Categoria categoriaRecuperada) {
		System.out.println("[ Categoria ] " + categoriaRecuperada.getNome());
		categoriaRecuperada.adicionarLivro(livro);
		livro.getCategorias().set(livro.getCategorias().indexOf(categoriaAntiga), categoriaRecuperada);
		adicionarCategoria(categoriaRecuperada);
	}

	@CacheEvict(cacheNames = { "categoriaBuscarTodasPorIdLivro" }, allEntries = true)
	private void adicionarCategoria(Categoria categoria) {
		categoriaRepository.save(categoria);
	}
}

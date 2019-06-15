package br.usjt.ccp3an_mca_projeto_integrado.controller;


import br.usjt.ccp3an_mca_projeto_integrado.model.*;
import br.usjt.ccp3an_mca_projeto_integrado.model.repository.IConteudoRepository;
import br.usjt.ccp3an_mca_projeto_integrado.service.IArquivoService;
import br.usjt.ccp3an_mca_projeto_integrado.service.ICategoriaService;
import br.usjt.ccp3an_mca_projeto_integrado.service.IConteudoService;
import br.usjt.ccp3an_mca_projeto_integrado.service.ITagService;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/conteudo")
public class ConteudoController {

	@Autowired
	IArquivoService arquivoService;
	
	@Autowired
	ICategoriaService categoriaService;
	
	@Autowired
	ITagService tagService;
	
	@Autowired
	IConteudoService conteudoService;

	@GetMapping("criar")
    public ModelAndView criar() {
		ModelAndView mv = new ModelAndView ("conteudo/criar");
		
		Map<TipoDeArquivo, List<Arquivo>> arquivosPorTipoDeArquivo = arquivoService.carregarArquivosPorTipoDeArquivo();
		List<Categoria> categorias = categoriaService.carregarCategorias();
		List<Tag> tags = tagService.carregarTag();
		
		mv.addObject("arquivosPorTipoDeArquivo", arquivosPorTipoDeArquivo);
		mv.addObject("categorias", categorias);
		mv.addObject("tags", tags);
		
		return mv;
    }
	
	@PostMapping("criar")
	public ModelAndView criar(@RequestParam("titulo") String titulo, @RequestParam("descricao") String descricao, 
							@RequestParam("arquivoId") Long arquivoId, @RequestParam("html") String html,
							@RequestParam("categoriaId") Long categoriaId, @RequestParam("listaTagsId") List<Long> listaTagsId,
							@RequestParam("tipoAcesso") Boolean tipoAcesso) throws FileNotFoundException{
		
		html = conteudoService.gerarHtml(html, arquivoService.carregarArquivoId(arquivoId), descricao);
		
		Conteudo conteudo = conteudoService.encapsular(titulo, descricao, arquivoId, html, categoriaId, 
														listaTagsId, tipoAcesso);
		
		conteudoService.inserir(conteudo);
		
		ModelAndView mv = new ModelAndView("conteudo/exibir");
		mv.addObject("html", conteudo.getHtml());
		mv.addObject("titulo", conteudo.getTitulo());
		
		return mv;
	}

	@PostMapping("/busca_descricao/")
	public ModelAndView buscarConteudo(@RequestParam("descricao") String descricao) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject(new Conteudo());
		List<Conteudo> conteudos = conteudoService.buscaPorDescricao(descricao);
		mv.addObject("conteudos", conteudos);
		return mv;
	}

	@GetMapping("/busca_categoria/{categoria}")
	public ModelAndView buscaPorCategoria(@PathVariable String categoria) {
		ModelAndView mv = new ModelAndView("index");
		mv.addObject(new Conteudo());
		List<Conteudo> conteudos = conteudoService.buscaPorCategoria(categoria);
		mv.addObject("conteudos", conteudos);
		return mv;
	}

	@GetMapping("/feedback/{feedback}/{id}")
	@ResponseBody
	public void feedback(@PathVariable String feedback, @PathVariable Long id) {
		if(feedback.equals("like")) {
			conteudoService.darLike(id);
		} else if(feedback.equals("dislike")) {
			conteudoService.darDislike(id);
		}
	}
}
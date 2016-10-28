ARQFONTE = principal.tex comandos.tex rosto.tex catalograficos.tex \
	preambulo.tex resumos.tex \
	introducao/introducao.tex \
	matematica/matematica.tex \
	figuras/figuras.tex \
	conclusao/conclusao.tex \
	apendice/apendice.tex

default: principal.dvi

simples:
	latex principal

principal.pdf: principal.ps
	ps2pdf -dMaxSubsetPct=100 -dCompatibilityLevel=1.2 -dSubsetFonts=true -dEmbedAllFonts=true -sPAPERSIZE=a4 principal.ps principal.pdf

principal.ps: principal.dvi
	dvips -Ppdf -t a4 -o principal.ps principal.dvi

principal.dvi: ${ARQFONTE} principal.bbl principal.gls
	latex principal
	latex principal

principal.bbl: principal.aux bibliografia.bib
	bibtex principal

principal.gls: principal.glo
	makeindex -s nomencl.ist -o principal.gls principal.glo

principal.aux: ${ARQFONTE}
	latex principal

principal.glo: ${ARQFONTE}
	latex principal

clean:
	rm -f *.aux *~ *.bak */*.aux */*~ */*.bak principal.bbl principal.blg principal.dvi principal.glo principal.gls principal.ilg principal.lof principal.log principal.lot principal.toc

realclean: clean
	rm -f principal.ps principal.pdf

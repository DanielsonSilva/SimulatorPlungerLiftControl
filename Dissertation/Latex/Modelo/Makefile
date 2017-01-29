# Comentário

default: pdflatex

all: pdf nomenclature bibtex

pdflatex:
	pdflatex -synctex=1 -interaction=nonstopmode main.tex

pdf:
	pdflatex main.tex

nomenclature: pdf
	makeindex main.nlo -s nomencl.ist -o main.nls
	pdflatex main.tex

bibtex: pdf
	bibtex main.aux
	pdflatex main.tex
	pdflatex main.tex

clean:
	rm -f  *.aux *~ *.bak */*.aux */*~ */*.bak */*/*.aux main.bbl main.blg main.dvi main.nlo main.nls main.ilg main.lof main.log main.lot main.toc

realclean: clean
	rm -f main.pdf

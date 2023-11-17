import kotlin.math.abs
import kotlin.random.Random
import kotlin.concurrent.timer

fun main() {
    var counter = 0
    val population = Population()

    println("Initial Configuration")
    displayChessboard(population[0].chromosome)
    println("\n")

    val interval = timer(period = 10) {
        counter++

        population.evolve()
        population.sortByDescending{ it.fitness }

        if ((calculateFitness(population[0].chromosome) == 28) || (counter == Population.GENERATIONS)) {
            println("Final Configuration")
            displayChessboard(population[0].chromosome)
            println("Best solution found in generation $counter")
            println("Queen positions: ${population[0].chromosome.contentToString()}")
            println("Fitness score: ${calculateFitness(population[0].chromosome)}")
            cancel()
        }
    }
}

class Population {
    private val individuals = initializePopulation(POPULATION_SIZE)

    companion object {
        const val POPULATION_SIZE = 10
        const val MUTATION_RATE = 0.1
        const val GENERATIONS = 10000
    }

    private fun initializePopulation(populationSize: Int): ArrayList<Individual> {
        val population = arrayListOf<Individual>()

        for (i in 0 until populationSize) {
            var chromosome = generateChromosome()
            var individual = Individual(chromosome)
            population.add(individual)
        }

        return population
    }

    fun evolve() {
        val newPopulation = mutableListOf<Individual>()

        val bestPopulation = individuals.take(individuals.size / 2)

        for (i in individuals.indices) {
            val parent1 = selectParent(bestPopulation)
            val parent2 = selectParent(bestPopulation)

            val child = crossover(parent1, parent2)

            mutate(child, MUTATION_RATE)

            newPopulation.add(child)
        }

        individuals.clear()
        individuals.addAll(newPopulation)
    }

    private fun selectParent(population: List<Individual>): Individual {
        val index1 = Random.nextInt(population.size)
        return population[index1]
    }

    private fun crossover(parent1: Individual, parent2: Individual): Individual {
        val crossoverPoint = Random.nextInt(8)
        val childChromosome = IntArray(8)

        for (i in 0 until crossoverPoint) {
            childChromosome[i] = parent1.chromosome[i]
        }

        for (i in crossoverPoint until 8) {
            childChromosome[i] = parent2.chromosome[i]
        }

        return Individual(chromosome = childChromosome)
    }

    private fun mutate(individual: Individual, mutationRate: Double) {
        for (i in 0 until 8) {
            if (Random.nextDouble() < mutationRate) {
                individual.chromosome[i] = Random.nextInt(8)
            }
        }
    }

    operator fun get(index: Int): Individual {
        return individuals[index]
    }

    fun <T : Comparable<T>> sortByDescending(selector: (Individual) -> T) {
        individuals.sortByDescending(selector)
    }

    override fun toString(): String {
        return "Population(individuals=$individuals)"
    }
}

fun generateChromosome(): IntArray {
    val chromosome = IntArray(8)
    for (i in 0 until 8) {
        chromosome[i] = Random.nextInt(8)
    }
    return chromosome
}

fun calculateFitness(chromosome: IntArray): Int {
    var clashes = 0

    for (i in 0 until 8) {
        for (j in i + 1 until 8) {
            if (
                chromosome[i] == chromosome[j] ||
                abs(chromosome[i] - chromosome[j]) == abs(i - j)
            ) {
                clashes++
            }
        }
    }

    return 28 - clashes
}

fun displayChessboard(chromosome: IntArray) {
    for (i in 0 until 8) {
        for (j in 0 until 8) {
            val symbol = if (chromosome[i] == j) " Q " else " | "
            print("$symbol")
        }
        println()
    }
}

data class Individual(val chromosome: IntArray) {
    var fitness = calculateFitness(chromosome)
}


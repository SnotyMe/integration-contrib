package me.snoty.contrib.node.studyly

import me.snoty.backend.wiring.data.NodeInput
import me.snoty.backend.wiring.data.NodeOutput
import me.snoty.backend.wiring.data.iterableStructOutput
import me.snoty.backend.wiring.data.mapWithSettings
import me.snoty.backend.wiring.node.NodeHandleContext
import me.snoty.backend.wiring.node.NodeHandler
import me.snoty.backend.wiring.node.RegisterNode
import me.snoty.backend.wiring.node.metadata.NodeStereotype
import me.snoty.core.node.NodeWithSettings
import org.koin.core.annotation.Single

@RegisterNode(
	name = "studyly",
	displayName = "Studyly",
	stereotype = NodeStereotype.START,
	settingsType = StudylySettings::class,
	outputType = StudylyHomework::class,
)
@Single
class StudylyNodeHandler(private val api: StudylyAPI) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: NodeWithSettings, input: NodeInput): NodeOutput = input.mapWithSettings<StudylySettings>(node) { settings ->
		val items = api.getHomework(settings)

		iterableStructOutput(items)
	}
}

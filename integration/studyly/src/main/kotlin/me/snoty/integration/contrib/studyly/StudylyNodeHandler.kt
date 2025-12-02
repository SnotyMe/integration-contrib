package me.snoty.integration.contrib.studyly

import me.snoty.integration.common.annotation.RegisterNode
import me.snoty.integration.common.model.NodePosition
import me.snoty.integration.common.wiring.Node
import me.snoty.integration.common.wiring.NodeHandleContext
import me.snoty.integration.common.wiring.data.IntermediateData
import me.snoty.integration.common.wiring.data.NodeOutput
import me.snoty.integration.common.wiring.data.iterableStructOutput
import me.snoty.integration.common.wiring.data.mapWithSettings
import me.snoty.integration.common.wiring.node.NodeHandler
import org.koin.core.annotation.Single

@RegisterNode(
	name = "studyly",
	displayName = "Studyly",
	position = NodePosition.START,
	settingsType = StudylySettings::class,
	outputType = StudylyHomework::class,
)
@Single
class StudylyNodeHandler(private val api: StudylyAPI) : NodeHandler {
	context(ctx: NodeHandleContext)
	override suspend fun process(node: Node, input: Collection<IntermediateData>): NodeOutput = input.mapWithSettings<StudylySettings>(node) { settings ->
		val items = api.getHomework(settings)

		iterableStructOutput(items)
	}
}
